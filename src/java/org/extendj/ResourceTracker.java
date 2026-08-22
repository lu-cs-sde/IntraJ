/***************************************************************************
 * Copyright (C) 2026 The JastAdd Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.extendj;

import java.lang.management.ManagementFactory;
import com.sun.management.ThreadMXBean;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.ref.WeakReference;

import org.extendj.ast.Program;

/**
 * Generic resource tracking functionality
 *
 * Use {@link TH} for limited but fast tracking (~ hundreds of nanoseconds in 2026)
 * Use {@link Full} for detailed but slow tracking (~ tens of microseconds in 2026)
 */
public abstract class ResourceTracker<STATE> {
    private String reportPrefix;

    /**
     * @param reportPrefix: either the empty string or a prefix (e.g.,
     *   "total-") to prepend to the property name when reporting via
     *   <tt>ResourceTracker.report()</tt>
     */
    public ResourceTracker(String reportPrefix) {
        this.reportPrefix = reportPrefix;
    }

    /**
     * Start measurement region
     *
     * @return A state object that can be passed to {@link ResourceTracker.stop}
     */
    public abstract STATE start();

    /**
     * Stop measurement region
     *
     * Aggregates the delta between start and stop in <tt>this</tt>.
     *
     * @param A {@link State} object returned by {@link ResourceTracker.start}
     */
    public abstract void stop(STATE atStart);

    /**
     * Report statistics
     *
     * @param unprefixedReporter Consumer of the report information
     */
    public final void report(BenchReporter unprefixedReporter) {
        BenchReporter bench = BenchReporter.withPrefix(unprefixedReporter, this.reportPrefix);
        this.doReport(bench);
    }

    /**
     * Perform ResourceTracker-specific report printing
     */
    protected abstract void doReport(BenchReporter bench);


    /**
     * Convenience class for tracking execution time and heap allocation
     *
     * Intended for fine-grained measurements (broken down per individual analysis),
     * only checks counters that are cheap.
     *
     * {@link Full}
     */
    public static class TH extends ResourceTracker<THState> {
        protected THState aggregate = THState.zero();
        // Keep as weak reference so we don't interfere with GC if we accidentally keep the reference around
        protected WeakReference<Program> program;

        public TH(String prefix) {
            super(prefix);
        }

        public TH setProgram(Program program) {
            this.program = new WeakReference<Program>(program);
            return this;
        }

        /**
         * Start measurement region
         *
         * @return A {@link THState} object that can be passed to {@link ResourceTracker.stop}
         */
        @Override
        public THState start() {
            return THState.nowStart(program == null ? null : program.get());
        }

        /**
         * Stop measurement region
         *
         * Aggregates the delta between start and stop in <tt>this</tt>.
         *
         * @param A {@link THState} object returned by {@link ResourceTracker.start}
         */
        @Override
        public void stop(THState atStart) {
            THState atStop = THState.nowStop(program.get());
            this.aggregate.addDelta(atStart, atStop);
        }

        public THState getAggregate() {
            return this.aggregate;
        }

        @Override
        public String toString() {
            return aggregate.toString();
        }

        @Override
        protected void doReport(BenchReporter bench) {
            this.aggregate.report(bench);
        }
    }

    static class THState {
        long timeNanos = 0;
        long allocBytes = 0;
        long javaParseNanos = 0;
        long javaBytecodeNanos = 0;

        protected void setStateWithoutCurrentTime(Program p) {
            this.allocBytes = getAllocBytes();
            this.javaParseNanos = p == null? 0 : p.javaParseTime;
            this.javaBytecodeNanos = p == null? 0 : p.bytecodeParseTime;
        }

        protected void setCurrentTimeState() {
            this.timeNanos = System.nanoTime();
        }

        private THState() {
        }

        static THState nowStart(Program p) {
            THState retval = new THState();
            retval.setStateWithoutCurrentTime(p);
            // do this last
            retval.setCurrentTimeState();
            return retval;

        }

        static THState nowStop(Program p) {
            // do this first
            long time = System.nanoTime();
            THState retval = new THState();
            retval.timeNanos = time;
            retval.setStateWithoutCurrentTime(p);
            return retval;
        }

        void addDelta(THState atStart, THState atStop) {
            this.timeNanos          += atStop.timeNanos         - atStart.timeNanos;
            this.allocBytes         += atStop.allocBytes        - atStart.allocBytes;
            this.javaParseNanos     += atStop.javaParseNanos    - atStart.javaParseNanos;
            this.javaBytecodeNanos  += atStop.javaBytecodeNanos - atStart.javaBytecodeNanos;
        }

        static THState zero() {
            return new THState();
        }

        protected void report(BenchReporter bench) {
            bench.log("heap-bytes", allocBytes + "");
            bench.log("time", timeNanos + "");
            bench.log("java-parse-time", javaParseNanos + "");
            bench.log("java-bytecode-time", javaBytecodeNanos + "");
        }

        @Override
        public String toString() {
            return String.format("%16s ns\t%12s bytes", timeNanos, allocBytes);
        }
    }

    /**
     * Intended for per-iteration tracking, forces the "iter-" prefix
     */
    final static class Full extends ResourceTracker<FullState> {
        protected FullState aggregate = FullState.zero();
        protected WeakReference<Program> program = null;

        public Full(String prefix) {
            super(prefix);
        }

	@Override
	public FullState start() {
            return FullState.nowStart(this.program == null? null : this.program.get());
	}

        public Full setProgram(Program program) {
            this.program = new WeakReference<Program>(program);
            return this;
        }

	@Override
	public void stop(FullState atStart) {
            assert this.program != null;
            assert program.get() != null;
            FullState atStop = FullState.nowStop(program == null? null : program.get());
            this.aggregate.addFullDelta(atStart, atStop);
	}

	@Override
	protected void doReport(BenchReporter bench) {
            aggregate.report(bench);
	}
    }

    final static class FullState extends THState {
        protected long threadCPUTime;
        protected long processCPUTime;

        protected long gcCount;
        protected long gcTime;
        protected long jitTime;
        protected long classesLoaded;
        protected long classesUnloaded;

        FullState() { }

        public static FullState zero() {
            return new FullState();
        }

        @Override
        protected void setStateWithoutCurrentTime(Program program) {
            super.setStateWithoutCurrentTime(program);
            this.gcCount = totalGcCount();
            this.gcTime = totalGcNanos();
            this.jitTime = totalJITNanos();
            this.classesLoaded = totalClassesLoaded();
            this.classesUnloaded = totalClassesUnloaded();
        }

        static FullState nowStart(Program program) {
            FullState fs = new FullState();
            fs.setStateWithoutCurrentTime(program);
            fs.processCPUTime = processCPUNanos();
            fs.threadCPUTime = threadCPUNanos();
            fs.setCurrentTimeState();
            return fs;
        }

        static FullState nowStop(Program program) {
            long time = System.nanoTime();
            long processCpuTime = processCPUNanos();
            long threadCpuTime = threadCPUNanos();
            FullState retval = new FullState();
            retval.timeNanos = time;
            retval.threadCPUTime = threadCpuTime;
            retval.processCPUTime = processCpuTime;
            retval.setStateWithoutCurrentTime(program);
            return retval;
        }

        void addFullDelta(FullState atStart, FullState atStop) {
            this.addDelta(atStart, atStop);
            this.threadCPUTime   += atStop.threadCPUTime   - atStart.threadCPUTime;
            this.processCPUTime  += atStop.processCPUTime  - atStart.processCPUTime;
            this.gcCount         += atStop.gcCount         - atStart.gcCount;
            this.gcTime          += atStop.gcTime          - atStart.gcTime;
            this.jitTime         += atStop.jitTime         - atStart.jitTime;
            this.classesLoaded   += atStop.classesLoaded   - atStart.classesLoaded;
            this.classesUnloaded += atStop.classesUnloaded - atStart.classesUnloaded;
        }

        @Override
        protected void report(BenchReporter bench) {
            super.report(bench);
            bench.log("thread-cpu-time", threadCPUTime + "");
            bench.log("process-cpu-time", processCPUTime + "");
            bench.log("jit-time", jitTime + "");
            bench.log("gc-time", gcTime + "");
            bench.log("gc-count", gcCount + "");
            bench.log("classes-loaded", classesLoaded + "");
            bench.log("classes-unloaded", classesUnloaded + "");
        }
    }


    // ================================================================================
    // Management bean access: ThreadMXBean (heap usage)

    public static com.sun.management.ThreadMXBean threadManagement = null;
    static {
        threadManagement = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        if (threadManagement.isThreadCpuTimeSupported()
            && !threadManagement.isThreadCpuTimeEnabled()) {
            threadManagement.setThreadCpuTimeEnabled(true);
        }
    }
    static long getAllocBytes() {
        return threadManagement.getCurrentThreadAllocatedBytes();
    }

    // ================================================================================
    // Management bean access: all others
    static final GarbageCollectorMXBean[] garbageCollectors;
    static final MemoryManagerMXBean[] memoryManagers;
    static final MemoryPoolMXBean[] memoryPools;
    static final CompilationMXBean compilation;
    static final ClassLoadingMXBean classLoading;
    static final com.sun.management.OperatingSystemMXBean operatingSystem;

    static {
        garbageCollectors = ManagementFactory.getGarbageCollectorMXBeans().toArray(new GarbageCollectorMXBean[0]);
        memoryPools = ManagementFactory.getMemoryPoolMXBeans().toArray(new MemoryPoolMXBean[0]);
        compilation = ManagementFactory.getCompilationMXBean();
        classLoading = ManagementFactory.getClassLoadingMXBean();
        memoryManagers = ManagementFactory.getMemoryManagerMXBeans().toArray(new MemoryManagerMXBean[0]);
        java.lang.management.OperatingSystemMXBean os =
            ManagementFactory.getOperatingSystemMXBean();
        operatingSystem = os instanceof com.sun.management.OperatingSystemMXBean
            ? (com.sun.management.OperatingSystemMXBean) os : null;
    }

    /**
     * Actual thread CPU time
     */
    static long threadCPUNanos() {
        return threadManagement.getCurrentThreadCpuTime();
    }

    /**
     * Full process CPU time
     */
    static long processCPUNanos() {
        if (operatingSystem == null) {
            return 0;
        }
        return operatingSystem.getProcessCpuTime();
    }

    /**
     * Aggregate # of GCs
     */
    static long totalGcCount() {
        long sum = 0;
        for (GarbageCollectorMXBean bean : garbageCollectors) {
            sum += bean.getCollectionCount();
        }
        return sum;
    }

    /**
     * Aggregate nanoseconds reported by GCs
     */
    static long totalGcNanos() {
        long sum = 0; // millis
        for (GarbageCollectorMXBean bean : garbageCollectors) {
            sum += bean.getCollectionTime();
        }
        return sum * 1000;
    }

    /**
     * Aggregate nanoseconds reported by JIT
     */
    static long totalJITNanos() {
        if (!compilation.isCompilationTimeMonitoringSupported()) {
            return 0;
        }
        return compilation.getTotalCompilationTime() * 1000;
    }

    /**
     * Number of classes loaded since JVM started
     */
    static long totalClassesLoaded() {
        return classLoading.getTotalLoadedClassCount();
    }

    /**
     * Number of classes unloaded since JVM started
     */
    static long totalClassesUnloaded() {
        return classLoading.getUnloadedClassCount();
    }

    /**
     * Normalises a name used for reporting memory specs by removing whitespace
     */
    static String normalizeMemorySpecName(String m) {
        return "\"" + m.replace("\"", "'") + "\"";
    }

    static String memoryManagersSpec() {
        StringBuffer result = new StringBuffer("{ ");
        boolean firstM = true;
        for (MemoryManagerMXBean bean : memoryManagers) {
            if (firstM) {
                firstM = false;
            } else {
                result.append(", ");
            }
            String name = normalizeMemorySpecName(bean.getName());
            result.append(name);
            result.append(": { \"gc\": ");

            if (bean instanceof GarbageCollectorMXBean) {
                GarbageCollectorMXBean gc = (GarbageCollectorMXBean) bean;
                result.append("true, ");
                result.append("\"count\": ");
                result.append(gc.getCollectionCount());
                result.append(", \"totalTimeMillis\": ");
                result.append(gc.getCollectionTime());
            } else {
                result.append("false");
            }
            result.append(", \"pools\" : [");
            boolean first = true;
            for (String pname : bean.getMemoryPoolNames()) {
                if (first) {
                    first = false;
                } else {
                    result.append(", ");
                }
                result.append(normalizeMemorySpecName(pname));
            }
            result.append("] }");
        }
        result.append(" }");
        return result.toString();
    }

    /**
     * Constructs informative memory usage spec
     */
    static String memoryUsageSpec() {
        StringBuffer result = new StringBuffer("{ ");
        boolean firstM = true;
        for (MemoryPoolMXBean bean : memoryPools) {
            if (firstM) {
                firstM = false;
            } else {
                result.append(", ");
            }
            String name = normalizeMemorySpecName(bean.getName());
            result.append(name);
            result.append(": { \"heap\": ");
            result.append(bean.getType() == MemoryType.HEAP);

            // Based on the most recent collection
            MemoryUsage usage = bean.getUsage();

            if (usage != null) {
                result.append(", \"usage\" : [ ");
                result.append(usage.getInit());
                result.append(", ");
                result.append(usage.getUsed());
                result.append(", ");
                result.append(usage.getCommitted());
                result.append(", ");
                result.append(usage.getMax());
                result.append(" ]");
            }
            result.append(" }");
        }
        result.append(" }");
        return result.toString();
    }
}
