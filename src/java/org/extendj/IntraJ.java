/* Copyright (c) 2021, Idriss Riouak <idriss.riouak@cs.lth.se>
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.util.HashMap;

import org.extendj.ast.Warning;
import org.extendj.ast.CFGRoot;
import org.extendj.ast.CompilationUnit;
import org.extendj.ast.Frontend;
import org.extendj.ast.Program;
import org.extendj.ast.Problem;
import org.extendj.ast.StaticAnalysis;
import org.extendj.flow.utils.IJGraph;
import org.extendj.flow.utils.Utils;


/**
 * Perform static semantic checks on a Java program.
 */
public class IntraJ extends Frontend {
    static final String INTRAJ_VERSION = "0.1.0-" + Provenance.INTRAJ_COMMIT;

    static final Action HELP_ACTION = new HelpAction();
    static final Action ANALYSIS_ACTION = new AnalysisAction();
    static final Action BENCHMARK_ACTION = new BenchmarkAction();
    static Action action = HELP_ACTION;
    static boolean printStats = false;
    static LinkedHashSet<StaticAnalysis> analysesActive = new LinkedHashSet<>();
    static int benchIterNum = 10; // Number of iterations when benchmarking
    static boolean frontendErrorScan = false; // Replicate sle24-intraj behaviour
    static boolean runGCBetweenIterations = true; // Run System.gc() between iterations?

    static CLIOptions cliOptions = new CLIOptions();

    /**
     * indicate interest in benchmark action
     */
    static void analysisAction() {
        // unless the user has explicitly selected benchmarking
        if (action != BENCHMARK_ACTION) {
            action = ANALYSIS_ACTION;
        }
    }

    static {
        cliOptions
            .flag("-help",          "Prints this help text",
                v -> { action = HELP_ACTION; }).withShort('h')
            .flag("-version",       "Prints the IntraJ version number and exits",
                v -> { action = new VersionAction(); }).withShort('V')
            .flag("-buildinfo",     "Prints detailed IntraJ build provenance information",
                v -> { action = new ProvenanceAction(); })
            .flag("-genpdf",        "Generates a PDF with AST structure of the files under analysis (see also 'pred' and 'succ')",
                v -> { action = new PDFAction(); })
            .flag("-list-analyses", "List all analyses in machine-readable format",
                v -> { action = new ListAnalysesAction(); })
            .flag("-bench",         "Benchmark the specified program analyses over all compilation units for '-niter' runs",
                v -> { action = BENCHMARK_ACTION; })
            .flag("-statistics",    "Print analysis statistics",
                v -> { printStats = true; })
            .flag("-Wfrontend",     "Enables frontend warnings (ExtendJ behaviour but not sle24-intraj behaviour)",
                v -> { frontendErrorScan = true; })
            .option("-iter-gc",     "Benchmarking: run GC between iterations (0 or 1, default 1)",
                v -> { runGCBetweenIterations = Integer.parseInt(v) != 0; })
            .flag("-Wall",          "Enables all analyses (variant implementations only via -W<analysis>)",
                v -> { analysisAction(); analysesActive.addAll(StaticAnalysis.analyses()); })
            .option("-niter",       "Number of iterations for benchmarking (default " + benchIterNum + ")",
                v -> { benchIterNum = Integer.parseInt(v); })
            ;

        for (StaticAnalysis analysis: StaticAnalysis.analyses()) {
            cliOptions.flag("-W" + analysis.name(), null, v -> {
                analysesActive.add(analysis);
                analysisAction();
            });
        }

        cliOptions
            .passthrough("-nowarn",        false, "Disable ExtendJ warnings")
            .passthrough("-verbose",       false, "ExtendJ frontend: be verbose")
            .passthrough("-classpath",     true,  "ExtendJ frontend: classpath")
            .passthrough("-cp",            true,  null)
            .passthrough("-source",        true,  "ExtendJ frontend: source language version")
            .passthrough("-sourcepath",    true,  "ExtendJ frontend: source file path")
            .passthrough("-bootclasspath", true,  "ExtendJ frontend: standard library classpath")
            .passthrough("-extdirs",       true,  "ExtendJ frontend: JDK extension paths");
    }

    static boolean pdfPred = false; // include pred edges in PDF
    static boolean pdfSucc = false; // include succ edges in PDF
    public static boolean excludeLiteralsAndNull = false; // DAA: don't report on dead literal/succ assignment
    static {
        cliOptions
            .flag("-succ",       "PDF: include CFG successor edges",
                v -> { pdfSucc = true; })
            .flag("-pred",       "PDF: include CFG predecessor edges",
                v -> { pdfPred = true; })
            .flag("-excludelit", "DAA (Dead Assignment Analysis): don't flag dead assignments of literal values",
                v -> { excludeLiteralsAndNull = true; });
    }

    private static IJGraph graph;
    public static Object DrAST_root_node;

    // private static Analysis analysis = Analysis.getAnalysisInstance();
    // public static IntraJ intraj;

    /**
     * @return <tt>true</tt> if there were command line errors
     */
    static boolean parseOptions(String[] args) {
        cliOptions.parse(args);
        if (cliOptions.printErrors(System.err)) {
            return true;
        }
        return false;
    }

    /**
     * Entry point for the Java checker.
     * @param args command-line arguments
     */
    public static void main(String args[])
      throws FileNotFoundException, InterruptedException, IOException {

        if (parseOptions(args)) {
            System.exit(1);
        }
        int exitCode = action.exec();

        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    private static void printProgramStatistics(Program _program) {
        Program program = _program;
        Integer nNodes = 0, nEdges = 0;
        Integer maxNodes = 0, maxEdges = 0;

        for (CFGRoot r : program.CFGRoots()) {
            int resNodes = r.numbCFGNode();
            int resEdges = r.numbEdges();
            nNodes += resNodes;
            nEdges += resEdges;
            if (resNodes > maxNodes) {
                maxNodes = resNodes;
            }
            if (resEdges > maxEdges) {
                maxEdges = resEdges;
            }
        }
        Utils.printStatistics(System.out,
            "Number roots: " + program.CFGRoots().size());
        Utils.printStatistics(System.out, "Number CFGNodes: " + nNodes);
        Utils.printStatistics(System.out, "Number Edges: " + nEdges);
        Utils.printStatistics(System.out,
            "Largest CFG in terms of nodes: " + maxNodes);
        Utils.printStatistics(System.out,
            "Largest CFG in terms of edges: " + maxEdges);

        int cuNum = 0;
        Iterator<CompilationUnit> cuIt = program.compilationUnitIterator();
        while (cuIt.hasNext()) {
            ++cuNum;
            cuIt.next();
        }
        Utils.printStatistics(System.out,
            "Number compilation units: " + cuNum);
    }

    /**
     * Initialize the Java checker.
     */
    public IntraJ() {
        super("IntraJ-" + INTRAJ_VERSION, ExtendJVersion.getVersion());
        this.program = new Program();
        DrAST_root_node = getEntryPoint();
    }

    /**
     * Clears all references to the entry point
     *
     * After this call, the IntraJ object is no longer "useful" in any meaningful
     * fashion.
     *
     * Intended for use during benchmarking to ensure heap eviction.
     */
    public void clearEntrypoint() {
        this.program = null;
        DrAST_root_node = null;
    }

    /**
     * @param args command-line arguments
     * @return {@code true} on success, {@code false} on error
     * @deprecated Use run instead!
     */
    @Deprecated
    public static boolean compile(String args[]) {
        return 0 == new JavaChecker().run(args);
    }

    int run_iteration = 0;

    /**
     * Run the Java checker.
     * @param args command-line arguments
     * @return 0 on success, 1 on error, 2 on configuration error, 3 on system
     */
    public int run(String[] args) {
        return run(args, Program.defaultBytecodeReader(),
            Program.defaultJavaParser());
    }

    /**
     * Runs ExtendJ frontend with the command line configuraiton
     */
    public int runFrontendWithConfig() {
        String[] jCheckerOptions = cliOptions.getExtendJOptions();
        startTrackingFrontendResources();
        int result = run(jCheckerOptions);
        stopTrackingFrontendResources();
        return result;
    }

    /**
     * Clear analysis profilers and warning processors
     */
    protected static void resetAnalysisCounters(boolean withTracking, boolean withPrinting) {
        analysisResources = new TreeMap<>();
        warningHandlers = new TreeMap<>();
        for (StaticAnalysis analysis: analysesActive) {
            ArrayList<WarningHandler> handlerList = new ArrayList<>();
            WarningHandlers handlers = new WarningHandlers();

            if (withTracking) {
                handlerList.add(handlers.counter);
                handlerList.add(handlers.collector);
                if (withPrinting) {
                    handlerList.add(handlers.printer);
                }
            }

            handlers.joint = new WarningHandler.Multi(handlerList.toArray(new WarningHandler[0]));
            warningHandlers.put(analysis, handlers);
            handlers.joint.reset();
        }
    }

    /**
     * Clear frontend profilers
     */
    protected static void resetFrontendCounters(IntraJ intraj) {
        frontendResources = new ResourceTracker.TH("frontend").setProgram(intraj.program);
        frontendCheckResources = new ResourceTracker.TH("frontend-check").setProgram(intraj.program);
        iterTracker = (new ResourceTracker.Full("iter")).setProgram(intraj.program);
        iterState = iterTracker.start();
    }

    // Track warnings:
    static class WarningHandlers {
        WarningHandler.Collect collector = new WarningHandler.Collect();
        WarningHandler.Count counter = new WarningHandler.Count();
        WarningHandler.Print printer = new WarningHandler.Print(System.out);
        WarningHandler joint = null; // Multi handler joining some or all of the above
    }

    static TreeMap<StaticAnalysis, WarningHandlers> warningHandlers = new TreeMap<>();

    // ------------------------------
    // Profiling:

    // Per benchmark iteration:
    static ResourceTracker.Full iterTracker = null;
    static ResourceTracker.FullState iterState = null;

    // Per analysis;
    static Map<StaticAnalysis, ResourceTracker.TH> analysisResources = new TreeMap<>();

    // These two track resources used by Beaver, ExtendJ etc. outside of any dependencies
    // triggered by the analysis calls:
    static ResourceTracker.THState frontendResourcesState = null;
    static ResourceTracker.TH frontendResources = null;
    // ...except for general-purpose static checks in the frontend, tracked here:
    static ResourceTracker.TH frontendCheckResources = null;

    /**
     * Are we currently executing program analysis code (as opposed to frontend code)?
     *
     * Used by tracers
     */
    public static boolean inAnalysis() {
        return frontendResourcesState == null;
    }

    static void stopTrackingFrontendResources() {
        if (frontendResources != null) {
            assert frontendResourcesState != null;
            frontendResources.stop(frontendResourcesState);
            frontendResourcesState = null;
        }
    }
    static void startTrackingFrontendResources() {
        if (frontendResources != null) {
            assert frontendResourcesState == null;
            frontendResourcesState = frontendResources.start();
        }
    }

    /**
     * FIXME[CR]: make protected in superclass instead of copying
     */
    protected static Collection<Problem> EMPTY_PROBLEM_LIST = Collections.emptyList();

    /**
     * Processes from-source compilation units by error-checking them.
     *
     * Taken from `extendj/java4/frontend/FrontendMain.jrag` and adapted
     *
     * @return zero on success, non-zero on error
     */
    protected int processCompilationUnit(CompilationUnit unit) throws Error {
        if (unit != null && unit.fromSource()) {
            try {
                Collection<Problem> errors = unit.parseErrors();
                Collection<Problem> warnings = EMPTY_PROBLEM_LIST;
                // Compute static semantic errors when there are no parse errors
                // or the recover from parse errors option is specified.
                if (errors.isEmpty() || program.options().hasOption("-recover")) {
                    if (frontendErrorScan) { // replicate ExtendJ behaviour
                        // sle24-intraj (Docker) does NOT run this code:
                        stopTrackingFrontendResources();
                        // we track this separately from "normal" frontend cost
                        ResourceTracker.THState start = frontendCheckResources.start();

                        errors = unit.errors();
                        warnings = unit.warnings();

                        frontendCheckResources.stop(start);
                        startTrackingFrontendResources();
                    }
                }
                if (!errors.isEmpty()) {
                    processErrors(errors, unit);
                    return EXIT_ERROR;
                } else {
                    if (!warnings.isEmpty() && !program.options().hasOption("-nowarn")) {
                        processWarnings(warnings, unit);
                    }
                }
            } catch (Error e) {
                System.err.println("Encountered error while processing " + unit.pathName());
                throw e;
            }
        }
        return EXIT_SUCCESS;
    }


    protected void analyzeCompilationUnit(CompilationUnit unit) {
        stopTrackingFrontendResources();
        final String fileName = unit.getClassSource().sourceName();
        for (StaticAnalysis analysis: analysesActive) {
            if (!analysisResources.containsKey(analysis)) {
                analysisResources.put(analysis, new ResourceTracker.TH(analysis.name()).setProgram(this.program));
            }
            WarningHandlers warningHandler = warningHandlers.get(analysis);
            ResourceTracker.TH tracker = analysisResources.get(analysis);
            ResourceTracker.THState start = tracker.start();
            Collection<? extends Warning> warnings = analysis.scan(unit);
            tracker.stop(start);
            for (Warning w: warnings) {
                warningHandler.joint.handle(fileName, w);
            }
        }
        startTrackingFrontendResources();
    }

    /**
     * Called for each from-source compilation unit with no errors.
     *
     * Callback from `super().run()`
     */
    protected void processNoErrors(CompilationUnit unit) {
        resetIntraJ.visitCompilationUnit(this, unit);
    }

    @Override
    protected String name() {
        return "IntraJ";
    }

    @Override
    protected String version() {
        return "IntraJ-" + INTRAJ_VERSION + "/ExtendJ-" + ExtendJVersion.getVersion();
    }

    public Program getEntryPoint() { return program; }

    private void generatePDF() throws IOException, InterruptedException {
        for (String filename : cliOptions.getInputFiles()) {
            graph = new IJGraph(pdfPred, pdfSucc);
            program.graphLayout(graph);
            program.printGraph(graph);
            Utils.printInfo(System.out, "CFG rendering");
            graph.generatePDF(filename);
            Utils.printInfo(System.out, "DOT to PDF");
            ArrayList<String> cmdLd = new ArrayList<String>();
            cmdLd.add("python3");
            cmdLd.add("resources/pdf_merger.py");
            cmdLd.add(IJGraph.changeExtension(filename, ""));
            ProcessBuilder pb = new ProcessBuilder(cmdLd);
            Process process = pb.start();
            process.getOutputStream().close();
            process.waitFor();

            Utils.printInfo(System.out, "PDF file generated correctly");
        }
    }

    /**
     * Print usage help
     */
    static void printUsageInfo() {
        System.out.println("Usage: java -jar intraj.jar <action>  [options] <file_0> ... <file_n>");
        System.out.println("\nOptions:");
        cliOptions.printHelp(System.out);
        System.out.println("  -W<analysis> enables <analysis>");
        System.out.println("\nList of program analyses:");
        StaticAnalysis.printAnalyses(System.out);
    }

    /**
     * Action that the main entry point should execute
     */
    interface Action {
        public int exec();
    }

    /**
     * Action: print help
     */
    static class HelpAction implements Action {
        @Override
        public int exec() {
            printUsageInfo();
            return 0;
        }
    }

    /**
     * Action: print version information
     */
    static class VersionAction implements Action {
        @Override
        public int exec() {
            IntraJ intraj = new IntraJ();
            System.out.println(intraj.version());
            return 0;
        }
    }

    /**
     * Action: print all analyses in a machine readable format
     */
    static class ListAnalysesAction implements Action {
        @Override
        public int exec() {
            for (StaticAnalysis analysis: StaticAnalysis.analyses()) {
                String category = analysis.category();
                if (category.length() == 0) {
                    category = "local-pattern";
                }
                category = category.replace(' ', '-');
                // Fourth column: the analysis that this one implements.  Equal to the
                // first column for canonical analyses, so consumers that only read the
                // first three columns are unaffected.
                System.out.println(analysis.name() + "\t" + category + "\t"
                                   + analysis.description() + "\t"
                                   + analysis.canonicalName());
            }
            return 0;
        }
    }

    /**
     * Action: print detailed build provenance information
     */
    static class ProvenanceAction implements Action {
        @Override
        public int exec() {
            System.out.println("IntraJ  \tversion    \t" + INTRAJ_VERSION);
            System.out.println("IntraJ  \ttracing    \t" + Provenance.TRACING);
            System.out.println("IntraJ  \tvariant    \t" + Provenance.INTRAJ_VARIANT);
            System.out.println("IntraJ  \tcommit     \t" + Provenance.INTRAJ_COMMIT);
            System.out.println("IntraJ  \tcommit-date\t" + Provenance.INTRAJ_COMMIT_DATE);
            System.out.println("IntraJ  \tlog-format\tlong"); // long log format
            System.out.println("ExtendJ \tcommit     \t" + Provenance.EXTENDJ_COMMIT);
            System.out.println("IntraCFG\tcommit     \t" + Provenance.INTRACFG_COMMIT);
            System.out.println("IntraJ  \tjar        \t" + Provenance.JASTADD2_JAR);
            System.out.println("JastAdd2\tsha256     \t" + Provenance.JASTADD2_JAR_SHA256);
            System.out.println("JastAdd2\toptions    \t" + Provenance.JASTADD2_OPTIONS);
            return 0;
        }
    }

    /**
     * Action: print PDF with AST (and optionally parts of CFG)
     */
    static class PDFAction implements Action {
        @Override
        public int exec() {
            IntraJ intraj = new IntraJ();
            resetAnalysisCounters(false, false);
            resetFrontendCounters(intraj);
            intraj.runFrontendWithConfig();
            try {
                intraj.generatePDF();
            } catch (IOException exn) {
                exn.printStackTrace();
                return 1;
            } catch (InterruptedException exn) {
                exn.printStackTrace();
                return 1;
            }
            return 0;
        }
    }

    static String tracerToRun = null;

    static {
        cliOptions
            .option("-trace",         "Enable a tracer (FullEventTracer, SummaryTracer)",
                v -> { tracerToRun = v; });
    }

    /**
     * Action: Run analyses
     */
    static class AnalysisAction implements Action {
        @Override
        public int exec() {
            IntraJ intraj = new IntraJ();
            // Install tracing, if desired
            Tracer tracer = Tracer.traceMaybe(intraj.getEntryPoint(), tracerToRun);
            resetAnalysisCounters(true, true);
            resetFrontendCounters(intraj);
            intraj.runFrontendWithConfig();
            printStats(intraj);
            if (tracer != null) {
                // Give the tracer the opportunity to flush
                tracer.finish();
            }
            return 0;
        }

        protected void printExtraStats(IntraJ intraj) {
                for (StaticAnalysis analysis: analysesActive) {
                    Utils.printStatistics(System.out,
                        String.format("%-20s\t%20s ns",
                            analysis.name(),
                            analysisResources.get(analysis)));
                    WarningHandlers warningHandler = warningHandlers.get(analysis);
                    Utils.printStatistics(System.out, "warnings\t" + warningHandler.counter.get());
                    Utils.printStatistics(System.out, "md5\t" + warningHandler.collector.md5());
                }
        }

        protected void printStats(IntraJ intraj) {
            if (printStats) {
                printProgramStatistics(intraj.getEntryPoint());
                printExtraStats(intraj);
            }
        }
    }

    /**
     * Concatenated, comma-separated names of all active analyses
     *
     * Written out because older ExtendJ/CAT struggles with the concise stream() variant of this
     */
    static String analysesString() {
        StringBuffer analysesStr = new StringBuffer();
        boolean first = true;
        for (StaticAnalysis a : analysesActive) {
            if (first) {
                first = false;
            } else {
                analysesStr.append(",");
            }
            analysesStr.append(a.name());
        }
        return analysesStr.toString();
    }

    /**
     * Action: Benchmark analysis execution
     */
    static class BenchmarkAction extends AnalysisAction {
        int benchRun = 0;

        @Override
        public int exec() {
            BenchReporter.RUN.log("gc-explicit-reset", runGCBetweenIterations? 1 : 0);
            BenchReporter.RUN.log("reset", resetIntraJ.toString());
            BenchReporter.RUN.log("analyses", analysesString());

            for (StaticAnalysis analysis: analysesActive) {
                for (String abstractAnalysis: analysis.implementedAnalyses()) {
                    BenchReporter.RUN.logMap("analysis-variant", analysis.name(), abstractAnalysis);
                }
            }

            ResourceTracker.logMemoryManagersSpec(BenchReporter.RUN);
            HashMap<String, BenchReporter.Iterated> analysisReporters = new HashMap<>();
            for (StaticAnalysis analysis: analysesActive) {
                analysisReporters.put(
                    analysis.name(),
                    BenchReporter.iter().subReporter(analysis.name()));
            }

            BenchReporter.Iterated frontendReporter = BenchReporter.iter().subReporter("frontend");
            BenchReporter.Iterated frontendCheckReporter = BenchReporter.iter().subReporter("frontend-check");
            BenchReporter.Iterated resetReporter = BenchReporter.reset();

            IntraJ intraj = null;
            resetAnalysisCounters(true, false);
            //final String initialMemoryUsageSpec = ResourceTracker.logMemoryUsageSpec();
            for (benchRun = 0; benchRun < benchIterNum; ++benchRun) {
                BenchReporter.iter().nextIteration(); // count iterations

                intraj = resetIntraJ.resetAndRun(intraj, resetReporter);
                iterTracker.stop(iterState);
                BenchReporter iterReporter = BenchReporter.iter();
                if (resetProfiler != null) {
                    resetProfiler.report(resetReporter);
                }
                ResourceTracker.logMemoryUsageSpec(iterReporter);

                iterReporter.logWallTime();
                iterTracker.report(iterReporter);
                frontendResources.report(frontendReporter);
                frontendCheckResources.report(frontendCheckReporter);

                for (StaticAnalysis analysis: analysesActive) {
                    BenchReporter r = analysisReporters.get(analysis.name());
                    WarningHandlers warningHandler = warningHandlers.get(analysis);

                    ResourceTracker.TH aRes = analysisResources.get(analysis);
                    if (aRes != null) {
                        aRes.report(r);
                    }
                    r.log("warnings-num", warningHandler.counter.get());
                    r.log("warnings-md5", warningHandler.collector.md5().toString());
                }
                resetAnalysisCounters(true, false);
            }
            if (intraj != null) {
                printStats(intraj);
            }
            return 0;
        }

        // @Override
        // public void benchLog(String subId, String property, String value) {

        // }

        @Override
        protected void printExtraStats(IntraJ intraj) {
            // parent class stats aren't very useful for us
        }
    }

    // ---- IntraJ benchmarking reset strategies

    // ResourceTracker.memoryUsageSpec at the point of the last runGCIfRequested invocation (after GC, if triggered)
    static ResourceTracker.Full resetProfiler = null;

    /**
     * Runs GC if this is part of the experimental setup
     *
     * @return <tt>true</tt> iff gc was run
     */
    static boolean runGCIfRequested(BenchReporter resetReporter) {
        ResourceTracker.Full profiler = new ResourceTracker.Full("reset");
        ResourceTracker.FullState state = profiler.start();
        boolean result;

        if (runGCBetweenIterations) {
            System.gc();
            result = true;
        } else {
            result = false;
        }
        profiler.stop(state);
        ResourceTracker.logMemoryUsageSpec(resetReporter);
        resetProfiler = profiler;
        return result;
    }

    // ---- IntraJ benchmarking reset strategies

    /**
     * Governs how to prepare IntraJ prior to a second benchmarking run
     */
    static abstract class IntraJResetStrategy {
        static TreeMap<String, IntraJResetStrategy> strategies = new TreeMap<>();
        String name;
        public IntraJResetStrategy(String name) {
            this.name = name;
            strategies.put(name, this);
        }
        @Override
        public String toString() {
            return name;
        }

        static IntraJResetStrategy fromString(String name) {
            IntraJResetStrategy result = strategies.get(name);
            if (result == null) {
                throw new RuntimeException("Unknown strategy: " + name);
            }
            return result;
        }

        /**
         * Reset IntraJ instrance
         */
        public abstract IntraJ resetAndRun(IntraJ intraj, BenchReporter reporter);
        public abstract void visitCompilationUnit(IntraJ intraj, CompilationUnit cu);
    }

    static final IntraJResetStrategy INTRAJ_RESET_REPARSE = new IntraJResetStrategy("REPARSE") {

        @Override
        public IntraJ resetAndRun(IntraJ intraj, BenchReporter reporter) {
            clearOldIntraJ(intraj, reporter);
            intraj = new IntraJ();
            resetFrontendCounters(intraj);
            intraj.runFrontendWithConfig();
            return intraj;
        }

        void clearOldIntraJ(IntraJ intraj, BenchReporter reporter) {
            if (intraj == null) {
                return;
            }
            if (intraj.program == null) {
                throw new RuntimeException("IntraJ.program is null");
            }
            WeakReference<Program> tracker = new WeakReference<Program>(intraj.program);
            intraj.clearEntrypoint();
            if (runGCIfRequested(reporter)) { // true iff GC actually ran
                if (tracker.get() != null) {
                    throw new RuntimeException("Memory leak: old IntraJ program object was not collected");
                }
            }
        }

        @Override
        public void visitCompilationUnit(IntraJ intraj, CompilationUnit cu) {
            intraj.analyzeCompilationUnit(cu);
        }
    };

    static final IntraJResetStrategy INTRAJ_RESET_FLUSH_ALL = new IntraJResetStrategy("FLUSH-ALL") {
        ArrayList<CompilationUnit> cuList = new ArrayList<>();
        IntraJ intraj;

        @Override
        public IntraJ resetAndRun(IntraJ intraj, BenchReporter reporter) {
            if (intraj == null) {
                intraj = new IntraJ();
                runGCIfRequested(reporter);
                resetFrontendCounters(intraj);
                intraj.runFrontendWithConfig();
            } else {
                intraj.getEntryPoint().flushTreeCache();
                runGCIfRequested(reporter);
                resetFrontendCounters(intraj);
            }
            this.intraj = intraj;
            benchAll();
            return intraj;
        }

        void benchAll() {
            for (CompilationUnit cu: cuList) {
                intraj.analyzeCompilationUnit(cu);
            }
        }

        public void visitCompilationUnit(IntraJ intraj, CompilationUnit cu) {
            cuList.add(cu);
        }
    };

    static IntraJResetStrategy resetIntraJ = INTRAJ_RESET_REPARSE;

    static {
        cliOptions
            .option("-reset-method",         "Strategy for resetting IntraJ between benchmark runs, one of " + IntraJResetStrategy.strategies.keySet() + ", (default " + resetIntraJ + ")",
                v -> { resetIntraJ = IntraJResetStrategy.fromString(v); });
    }
}
