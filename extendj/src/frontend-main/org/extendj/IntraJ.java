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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;
import org.extendj.JavaChecker;
import org.extendj.ast.Analysis;
import org.extendj.ast.CFGNode;
import org.extendj.ast.CFGRoot;
import org.extendj.ast.CompilationUnit;
import org.extendj.ast.Frontend;
import org.extendj.ast.MethodDecl;
import org.extendj.ast.Program;
import org.extendj.ast.SmallSet;
import org.extendj.ast.WarningMsg;
import org.extendj.flow.utils.IJGraph;
import org.extendj.flow.utils.Utils;

/**
 * Perform static semantic checks on a Java program.
 */
public class IntraJ extends Frontend {

  public enum FlowProfiling { BACKWARD, FORWARD, COLLECTION, NONE, ALL }
  private static Boolean pred = false;
  private static Boolean succ = false;
  private static Boolean debug = false;
  private static Boolean pdf = false;
  private static Boolean multipleFiles = false;
  public static Boolean excludeLiteralsAndNull = false;
  private static IJGraph graph;
  private static String filename;
  public static Object DrAST_root_node;
  private static Integer numb_warning = 0;
  private static int n_iter = 0;
  private static boolean printTime = false;
  private static boolean statistics = false;
  private static long totalTime = 0;
  public static boolean toTxt = false;

  public static ArrayList<Analysis> analysis = new ArrayList<>();

  private String[] setEnv(String[] args) throws FileNotFoundException {
    if (args.length < 1) {
      System.err.println("You must specify a source file on the command line!");
      printOptionsUsage();
    }

    ArrayList<String> FEOptions = new ArrayList<>();

    filename = args[0];
    for (int i = 0; i < args.length; ++i) {
      String opt = args[i];
      if (opt.contains(".java")) {
        FEOptions.add(args[i]);
        continue;
      }
      if (opt.equals("-help")) {
        printOptionsUsage();
      } else if (opt.startsWith("-Wall")) {
        for (Analysis a : Analysis.values())
          analysis.add(a);
        continue;
      } else if (opt.equals("-txt-output")) {
        toTxt = true;
        continue;
      } else if (opt.startsWith("-Wexcept=")) {
        String an = opt.substring(9, opt.length());
        if (Arrays.asList(Analysis.names()).contains(an)) {
          analysis.remove(Analysis.valueOf(an));
          continue;
        }
          System.err.println("There is no analsis with name '" + an +
                             "' -wExcept");
          printOptionsUsage();

      } else if (opt.startsWith("-W")) {
        String an = opt.substring(2, opt.length());

        if (Arrays.asList(Analysis.names()).contains(an)) {
          analysis.add(Analysis.valueOf(an));
          continue;
        }
          System.err.println("There is no analsis with name '" + an + "' -W");
          printOptionsUsage();
      }
      switch (opt) {
      case "-succ":
        pdf = true;
        succ = true;
        break;
      case "-excludelit":
        excludeLiteralsAndNull = true;
        break;
      case "-pred":
        pdf = true;
        pred = true;
        break;
      case "-debug":
        debug = true;
        break;
      case "-helpfrontend":
        FEOptions.add("-help");
        break;
      case "-statistics":
        statistics = true;
        break;
      case "-nowarn":
        FEOptions.add("-nowarn");
        break;
      case "-genpdf":
        pdf = true;
        break;
      case "-verbose":
        FEOptions.add("-verbose");
        break;
      case "-classpath":
        FEOptions.add("-classpath");
        FEOptions.add(args[++i]);
        break;
      default:
        System.err.println("Unrecognized option: " + opt);
        printOptionsUsage();
      }
    }
    if (FEOptions.size() > 2) {
      multipleFiles = true;
    }
    return FEOptions.toArray(new String[FEOptions.size()]);
  }

  /**
   * Entry point for the Java checker.
   * @param args command-line arguments
   */
  public static void main(String args[])
      throws FileNotFoundException, InterruptedException, IOException {
    IntraJ intraj = new IntraJ();

    String[] jCheckerArgs = intraj.setEnv(args);
    int exitCode = intraj.run(jCheckerArgs);
    if (exitCode != 0) {
      System.exit(exitCode);
    }
      DrAST_root_node = intraj.getEntryPoint();
      if (pdf) {
        intraj.generatePDF();
      }
      if (debug)
        intraj.debug();

      if (statistics) {
        Utils.printStatistics(
            System.out, "Elapsed time (CFG + Dataflow): " + totalTime / 1000 +
                            "." + totalTime % 1000 + "s");
      }
      Utils.printStatistics(System.out,
      "Total number of warnings: " + numb_warning);
      if (statistics) {
        printProgramStatistics(intraj.getEntryPoint());
      }
  }

  private static void printProgramStatistics(Program _program) {
    Program program = _program;
    Integer nNodes = 0, nEdges = 0;
    Integer maxNodes = 0, maxEdges = 0;

    for (CFGRoot r : program.methods()) {
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
                          "Number roots: " + program.methods().size());
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
  public IntraJ() { super("IntraJ", ExtendJVersion.getVersion()); }

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
  public int run(String args[]) {
    return run(args, Program.defaultBytecodeReader(),
               Program.defaultJavaParser());
  }

  /**
   * Called for each from-source compilation unit with no errors.
   */
  protected void processNoErrors(CompilationUnit unit) {
    Integer nbrWrn = 0;
    for (Analysis a : analysis) {
      try {
        long startTime = System.currentTimeMillis();
        TreeSet<WarningMsg> wmgs = (TreeSet<WarningMsg>)unit.getClass()
                                       .getDeclaredMethod(a.toString())
                                       .invoke(unit);
        for (WarningMsg wm : wmgs) {
          if (analysis.contains(wm.getAnalysisType())) {
            wm.print(System.out);
            nbrWrn++;
          }
        }
        long dt = System.currentTimeMillis() - startTime;
        totalTime += dt;
      } catch (Throwable t) {
        t.printStackTrace();
        System.exit(1);
      }
    }
    numb_warning += nbrWrn;
  }

  @Override
  protected String name() {
    return "IntraJ";
  }

  @Override
  protected String version() {
    return "SCAM2021";
  }

  public Program getEntryPoint() { return program; }

  private void debug() {
    for (CFGRoot root : getEntryPoint().methods()) {
      root.entry().printSuccSets(System.out,
                                 SmallSet.<CFGNode>empty().<CFGNode>mutable());
    }
    System.out.println("-----------");
    for (CFGRoot root : getEntryPoint().methods()) {
      root.exit().printPredSets(System.out,
                                SmallSet.<CFGNode>empty().<CFGNode>mutable());
    }
  }

  private void generatePDF() throws IOException, InterruptedException {
    graph = new IJGraph(pred, succ);
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

  void printOptionsUsage() {
    System.out.println(name() + " - Version:" + version());
    System.out.println("Available options:");
    System.out.println("  -help: prints all the available options.");
    System.out.println(
        "  -genpdf: generates a pdf with AST structure of all the methods in the analysed files. It can be used combined with `-succ`,`-pred`.");
    System.out.println(
        "  -succ: generates a pdf with the successor relation for all the methods in the analysed files. It can be used combined with `-pred`.");
    System.out.println(
        "  -pred: generates a pdf with the predecessor relation for all the methods in the analysed files. It can be used combined with `-succ`.");
    System.out.println(
        "  -statistics: prints the number of CFGRoots, CFGNodes and CFGEdges in the analysed files.");
    System.out.println("  -nowarn: the warning messages are not printed.");

    System.out.println("-------------- ANALYSIS OPTIONS --------------------");
    System.out.println("Available analysis (ID):");
    System.out.println("  DAA: Detects unused `dead` assignments");
    System.out.println(
        "  NPA: Detects occurrences of Null Pointer Dereferenciation");
    System.out.println(
        "  -WID: enable the analysis with the respective ID, e.g., -WDAA");
    System.out.println("  -Wall: enables all the available analysis");
    System.out.println(
        "  -Wexcept=ID: enable all the available analysis except ID.");
    System.out.println(
        "  -niter=x: runs the analysis `x` times and prints the time necessary to execute an analysis.");
    System.exit(1);
  }
}
