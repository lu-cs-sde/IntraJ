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
import java.lang.management.MemoryUsage;
import java.lang.ref.WeakReference;
import java.util.OptionalLong;

import org.extendj.ast.Program;

/**
 * Generic resource tracking functionality
 *
 * Use {@link TH} for limited but fast tracking (~ hundreds of nanoseconds in 2026)
 * Use {@link Full} for detailed but slow tracking (~ tens of microseconds in 2026)
 */
public abstract class ResourceTracker<STATE> {
    protected String name;

    /**
     * @param name A string name used for debugging
     */
    public ResourceTracker(String name) {
        this.name = name;
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
    public final void report(BenchReporter reporter) {
        this.doReport(reporter);
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

        public TH(String name) {
            super(name);
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
            //System.err.println("-- start: " + this.name);
            THState result = THState.nowStart(program == null ? null : program.get());
            return result;
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
            //System.err.println("-- stop: " + this.name);
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
        long bytecodeParseNanos = 0;

        protected void setStateWithoutCurrentTime(Program p) {
            this.allocBytes = getAllocBytes();
            this.javaParseNanos = p == null? 0 : p.javaParseTime;
            this.bytecodeParseNanos = p == null? 0 : p.bytecodeParseTime;
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
            this.timeNanos           += atStop.timeNanos          - atStart.timeNanos;
            this.allocBytes          += atStop.allocBytes         - atStart.allocBytes;
            this.javaParseNanos      += atStop.javaParseNanos     - atStart.javaParseNanos;
            this.bytecodeParseNanos  += atStop.bytecodeParseNanos - atStart.bytecodeParseNanos;
        }

        static THState zero() {
            return new THState();
        }

        protected void report(BenchReporter bench) {
            bench.log("alloc-bytes", allocBytes);
            bench.log("time", timeNanos);
            bench.log("java-parse-time", javaParseNanos);
            bench.log("bytecode-parse-time", bytecodeParseNanos);
        }

        @Override
        public String toString() {
            return String.format("%16s ns\t%12s bytes", timeNanos, allocBytes);
        }
    }

    /**
     * Intended for per-iteration for the "iter" prefix
     */
    final static class Full extends ResourceTracker<FullState> {
        protected FullState aggregate = FullState.zero();
        protected WeakReference<Program> program = null;

        public Full(String name) {
            super(name);
        }

	@Override
	public FullState start() {
            //System.err.println("--=--start: " + this.name);
            FullState result = FullState.nowStart(this.program == null? null : this.program.get());
            return result;
	}

        public Full setProgram(Program program) {
            this.program = new WeakReference<Program>(program);
            return this;
        }

	@Override
	public void stop(FullState atStart) {
            //System.err.println("--=--stop: " + this.name);
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
        protected OptionalLong processCPUTime = OptionalLong.of(0);

        protected long gcCount;
        protected long gcTime;
        protected long classesLoaded;
        protected long classesUnloaded;
        protected OptionalLong jitTime = OptionalLong.of(0);

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
            OptionalLong processCpuTime = processCPUNanos();
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
            this.gcCount         += atStop.gcCount         - atStart.gcCount;
            this.gcTime          += atStop.gcTime          - atStart.gcTime;
            this.classesLoaded   += atStop.classesLoaded   - atStart.classesLoaded;
            this.classesUnloaded += atStop.classesUnloaded - atStart.classesUnloaded;
            this.jitTime
              = plusDiff(this.jitTime,             atStop.jitTime,        atStart.jitTime);
            this.processCPUTime
              = plusDiff(this.processCPUTime,      atStop.processCPUTime, atStart.processCPUTime);
        }

        @Override
        protected void report(BenchReporter bench) {
            super.report(bench);
            bench.log("thread-cpu-time", threadCPUTime);
            bench.log("process-cpu-time", processCPUTime);
            bench.log("jit-time", jitTime);
            bench.log("gc-time", gcTime);
            bench.log("gc-count", gcCount);
            bench.log("classes-loaded", classesLoaded);
            bench.log("classes-unloaded", classesUnloaded);
        }
    }

    /**
     * @return base + (stop - start), or OptionalLong.empty() if any parameter is empty
     */
    static OptionalLong plusDiff(OptionalLong base, OptionalLong stop, OptionalLong start) {
        if (!(stop.isPresent() && start.isPresent() && base.isPresent())) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(base.getAsLong() + stop.getAsLong() - start.getAsLong());
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
    static OptionalLong processCPUNanos() {
        if (operatingSystem == null) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(operatingSystem.getProcessCpuTime());
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
        long sum = 0; // ms
        for (GarbageCollectorMXBean bean : garbageCollectors) {
            sum += bean.getCollectionTime();
        }
        return sum * 1_000_000; // scale to ns
    }

    /**
     * Aggregate nanoseconds reported by JIT
     */
    static OptionalLong totalJITNanos() {
        if (!compilation.isCompilationTimeMonitoringSupported()) {
            return OptionalLong.empty();
        }
        return OptionalLong.of(compilation.getTotalCompilationTime() // ms
                               * 1_000_000);  // scale to ns
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
        return m.replaceAll(" ", "-").replaceAll("[^A-Za-z0-9_-]", "");
    }

    /**
     * Report the overall memory pool and memory manager layout structure
     */
    public static void logMemoryManagersSpec(BenchReporter reporter) {
        for (MemoryManagerMXBean bean : memoryManagers) {
            String name = bean.getName();
            String nname = normalizeMemorySpecName(name);

            StringBuffer pools = new StringBuffer();
            for (String poolName : bean.getMemoryPoolNames()) {
                pools.append(normalizeMemorySpecName(poolName));
                pools.append(" ");
            }

            reporter.logMap("jvm-mem-manager", nname,
                "pools=[ " + pools.toString() + "]"
                + ", name=" + name);
        }

        for (MemoryPoolMXBean bean : memoryPools) {
            String name = bean.getName();
            String nname = normalizeMemorySpecName(name);

            reporter.logMap("jvm-mem-pool", nname,
                "type=" + bean.getType()
                + ", name=" + name);

        }
    }

    static String liftedLongHex(long v) {
        if (v < 0) {
            return "U";
        }
        return String.format("0x%x", v);
    }

    /**
     * Report the current memory usage structure
     */
    public static void logMemoryUsageSpec(BenchReporter reporter) {
        for (MemoryPoolMXBean bean : memoryPools) {
            String name = bean.getName();
            String nname = normalizeMemorySpecName(name);

            MemoryUsage usage = bean.getUsage();

            if (usage == null) {
                reporter.logMap("jvm-mem-pool-usage", nname, "U");
            } else {
                reporter.logMap("jvm-mem-pool-usage",
                    nname,
                    liftedLongHex(usage.getInit())
                    + "\t" + liftedLongHex(usage.getUsed())
                    + "\t" + liftedLongHex(usage.getCommitted())
                    + "\t" + liftedLongHex(usage.getMax()));
            }
        }
    }
}
