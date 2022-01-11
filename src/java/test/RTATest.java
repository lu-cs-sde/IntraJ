// package test;

// import java.io.ByteArrayOutputStream;
// import java.io.File;
// import java.io.PrintStream;
// import java.util.Map;
// import java.util.Set;
// import org.extendj.IntraJ;
// import org.extendj.JavaChecker;
// import org.extendj.ast.AbsRef;
// import org.extendj.ast.Analysis;
// import org.extendj.ast.AssignExpr;
// import org.extendj.ast.CFGNode;
// import org.extendj.ast.CFGRoot;
// import org.extendj.ast.CompilationUnit;
// import org.extendj.ast.ConstructorDecl;
// import org.extendj.ast.Exit;
// import org.extendj.ast.MethodDecl;
// import org.extendj.ast.PostIncExpr;
// import org.extendj.ast.Program;
// import org.extendj.ast.SmallSet;
// import org.extendj.ast.Variable;
// import org.extendj.ast.WarningMsg;
// import org.extendj.flow.utils.IJGraph;
// import org.junit.Assert;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.junit.runners.Parameterized;
// import org.junit.runners.Parameterized.Parameters;

// @RunWith(Parameterized.class)
// public class RTATest {
//   static void printDecls(CFGRoot n, PrintStream outStream) {
//     outStream.println(n.getName() + ":{");
//     for (CFGRoot m : n.allFunctionCalls()) {
//       outStream.println("\t" + m.getName());
//     }
//     outStream.println("}");
//   }

//   public static final File TEST_DIRECTORY =
//       new File("testfiles/Interprocedural/RTA");

//   private final String filename;

//   public RTATest(String testFile) { filename = testFile; }

//   @Test
//   public void runTest() throws Exception {
//     PrintStream out = System.out;
//     try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
//          PrintStream outStream = new PrintStream(baos)) {
//       String[] args = {filename};
//       IntraJ intraj = new IntraJ();
//       int execCode = intraj.run(args);
//       if (1 == execCode) {
//         Assert.fail("Compilation errors found " + execCode);
//       }

//       //   SmallSet<CFGNode> nodes = SmallSet.mutable();
//       Program program = intraj.getEntryPoint();
//       System.setOut(new PrintStream(baos));
//       IJGraph graph = new IJGraph(true, true);
//       program.graphLayout(graph);
//       program.printGraph(graph);
//       for (CFGRoot fun : program.CFGRoots()) {
//         printDecls(fun, outStream);
//       }
//       UtilTest.compareOutput(
//           baos.toString(), new File(UtilTest.changeExtension(filename,
//           ".out")), new File(UtilTest.changeExtension(filename,
//           ".expected")));

//     } finally {
//       System.setOut(out);
//     }
//   }

//   @Parameters(name = "{0}")
//   public static Iterable<Object[]> getTests() {
//     return UtilTest.getTestParametersSubFolders(TEST_DIRECTORY, ".java");
//   }
// }