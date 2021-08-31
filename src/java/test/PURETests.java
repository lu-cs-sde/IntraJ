package test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;
import org.extendj.IntraJ;
import org.extendj.JavaChecker;
import org.extendj.ast.AbsRef;
import org.extendj.ast.Analysis;
import org.extendj.ast.AssignExpr;
import org.extendj.ast.CFGNode;
import org.extendj.ast.CFGRoot;
import org.extendj.ast.CompilationUnit;
import org.extendj.ast.ConstructorDecl;
import org.extendj.ast.Exit;
import org.extendj.ast.MethodDecl;
import org.extendj.ast.PostIncExpr;
import org.extendj.ast.Program;
import org.extendj.ast.SmallSet;
import org.extendj.ast.Variable;
import org.extendj.ast.WarningMsg;
import org.extendj.flow.utils.IJGraph;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PURETests {
  static void printExit(Exit n, PrintStream outStream) {
    for (Map.Entry<Variable, Set<AbsRef>> entry :
         n.PURE_out().getMapFun().entrySet()) {
      outStream.println("Variable: " + entry.getKey());
      for (AbsRef abs : entry.getValue()) {
        outStream.println("\t\t" + abs.toString());
      }
    }
  }

  public static final File TEST_DIRECTORY = new File("testfiles/DataFlow/PURE");

  private final String filename;

  public PURETests(String testFile) { filename = testFile; }

  @Test
  public void runTest() throws Exception {
    PrintStream out = System.out;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         PrintStream outStream = new PrintStream(baos)) {
      String[] args = {filename};
      IntraJ intraj = new IntraJ();
      int execCode = intraj.run(args);
      if (1 == execCode) {
        Assert.fail("Compilation errors found " + execCode);
      }

      SmallSet<CFGNode> nodes = SmallSet.mutable();
      Program program = intraj.getEntryPoint();
      System.setOut(new PrintStream(baos));
      IJGraph graph = new IJGraph(true, true);
      program.graphLayout(graph);
      program.printGraph(graph);
      for (CFGRoot fun : program.methods()) {
        printExit(fun.exit(), outStream);
      }
      // for (ConstructorDecl fun : program.constructors()) {
      //   // printNASets(fun.Entry(), outStream, nodes);
      // }
      // outStream.println();
      Integer nbrWrn = 0;
      //   for (CompilationUnit cu : program.getCompilationUnits()) {
      //     for (WarningMsg wm : cu.PURE()) {
      //       if (wm.getAnalysisType() == Analysis.NPA)
      //         wm.print(System.out);
      //       nbrWrn++;
      //     }
      //   }
      UtilTest.compareOutput(
          baos.toString(), new File(UtilTest.changeExtension(filename, ".out")),
          new File(UtilTest.changeExtension(filename, ".expected")));

    } finally {
      System.setOut(out);
    }
  }

  @Parameters(name = "{0}")
  public static Iterable<Object[]> getTests() {
    return UtilTest.getTestParametersSubFolders(TEST_DIRECTORY, ".java");
  }
}