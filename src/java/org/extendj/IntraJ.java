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
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.extendj.ast.Warning;
import org.extendj.ast.CFGRoot;
import org.extendj.ast.CompilationUnit;
import org.extendj.ast.Frontend;
import org.extendj.ast.Program;
import org.extendj.ast.StaticAnalysis;
import org.extendj.flow.utils.IJGraph;
import org.extendj.flow.utils.Utils;


/**
 * Perform static semantic checks on a Java program.
 */
public class IntraJ extends Frontend {
    static final String INTRAJ_VERSION = "0.1.0";

    static final Action HELP_ACTION = new HelpAction();
    static final Action ANALYSIS_ACTION = new AnalysisAction();
    static Action action = HELP_ACTION;
    static boolean printStats = false;
    static LinkedHashSet<StaticAnalysis> analysesActive = new LinkedHashSet<>();

    static CLIOptions cliOptions = new CLIOptions();
    static {
        cliOptions
            .flag("-help",       "Prints this help text",
                v -> { action = HELP_ACTION; }).withShort('h')
            .flag("-version",    "Prints the IntraJ version number and exits",
                v -> { action = new VersionAction(); }).withShort('V')
            .flag("-genpdf",     "Generates a PDF with AST structure of the files under analysis (see also 'pred' and 'succ')",
                v -> { action = new PDFAction(); })
            .flag("-statistics", "Print analysis statistics",
                v -> { printStats = true; })
            .flag("-Wall",       "Enables all analyses",
                v -> { action = ANALYSIS_ACTION; analysesActive.addAll(StaticAnalysis.analyses()); });

        for (StaticAnalysis analysis: StaticAnalysis.analyses()) {
            cliOptions.flag("-W" + analysis.name(), null, v -> {
                analysesActive.add(analysis);
                action = ANALYSIS_ACTION;
            });
        }

        cliOptions
            .passthrough("-nowarn",     false, "Disable ExtendJ warnings")
            .passthrough("-verbose",    false, "ExtendJ frontend: be verbose")
            .passthrough("-classpath",  true,  "ExtendJ frontend: classpath")
            .passthrough("-cp",         true,  null)
            .passthrough("-source",     true,  "ExtendJ frontend: source language version")
            .passthrough("-sourcepath", true,  "ExtendJ frontend: source file path");
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
    }

    /**
     * Initialize the Java checker.
     */
    public IntraJ() { super("IntraJ-" + INTRAJ_VERSION, ExtendJVersion.getVersion()); }

    /**
     * @param args command-line arguments
     * @return {@code true} on success, {@code false} on error
     * @deprecated Use run instead!
     */
    @Deprecated
    public static boolean compile(String args[]) {
        return 0 == new JavaChecker().run(args);
    }

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
        return run(jCheckerOptions);
    }

    protected void resetWarningHandlers() {
        warningHandler.reset();
    }

    protected void init() {
        this.program = new Program();
        DrAST_root_node = getEntryPoint();
        totalDurations = new TreeMap<>();
        resetWarningHandlers();
    }

    WarningHandler.Collect warningCollector = new WarningHandler.Collect();
    WarningHandler.Count warningCounter = new WarningHandler.Count();
    WarningHandler.Print warningPrinter = new WarningHandler.Print(System.out);
    WarningHandler warningHandler = new WarningHandler.Multi(warningCollector, warningCounter, warningPrinter);

    Map<StaticAnalysis, Long> totalDurations = new TreeMap<>();

    /**
     * Called for each from-source compilation unit with no errors.
     */
    protected void processNoErrors(CompilationUnit unit) {
        final String fileName = unit.getClassSource().sourceName();
        for (StaticAnalysis analysis: analysesActive) {
            if (!totalDurations.containsKey(analysis)) {
                totalDurations.put(analysis, 0L);
            }
            long startTime = System.nanoTime();
            Collection<? extends Warning> warnings = analysis.scan(unit);
            long timeDelta = System.nanoTime() - startTime;
            totalDurations.put(analysis, totalDurations.get(analysis) + timeDelta);
            for (Warning w: warnings) {
                warningHandler.handle(fileName, w);
            }
        }
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
     * Action: print PDF with AST (and optionally parts of CFG)
     */
    static class PDFAction implements Action {
        @Override
        public int exec() {
            IntraJ intraj = new IntraJ();
            intraj.init();
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

    /**
     * Action: Run analyses
     */
    static class AnalysisAction implements Action {
        @Override
        public int exec() {
            IntraJ intraj = new IntraJ();
            intraj.init();
            intraj.runFrontendWithConfig();
            if (printStats) {
                printProgramStatistics(intraj.getEntryPoint());
                for (StaticAnalysis analysis: analysesActive) {
                    Utils.printStatistics(System.out,
                        String.format("%-20s\t%20s ns",
                        analysis.name(),
                        intraj.totalDurations.get(analysis)));
                }
                Utils.printStatistics(System.out, "warnings\t" + intraj.warningCounter.get());
                Utils.printStatistics(System.out, "md5\t" + intraj.warningCollector.md5());
            }
            return 0;
        }
    }
}
