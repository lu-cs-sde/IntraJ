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

package test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import org.extendj.IntraJ;
import org.extendj.ast.CFGNode;
import org.extendj.ast.CFGRoot;
import org.extendj.ast.ConstructorDecl;
import org.extendj.ast.MethodDecl;
import org.extendj.ast.Program;
import org.extendj.ast.SmallSet;
import org.extendj.flow.utils.IJGraph;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CFGTests {
  public static final File TEST_DIRECTORY = new File("testfiles/CFG");
  private final String filename;
  public CFGTests(String testFile) { filename = testFile; }

  @Test
  public void runTest() throws Exception {
    PrintStream out = System.out;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         PrintStream outStream = new PrintStream(baos)) {
      String[] args = {filename};
      IntraJ jChecker = new IntraJ();
      int execCode = jChecker.run(args);
      if (1 == execCode) {
        Assert.fail("Compilation errors found " + execCode);
      }

      SmallSet<CFGNode> nodes = SmallSet.<CFGNode>empty().<CFGNode>mutable();
      Program program = jChecker.getEntryPoint();
      System.setOut(new PrintStream(baos));
      IJGraph graph = new IJGraph(true, true);
      program.graphLayout(graph);
      program.printGraph(graph);
      for (CFGRoot fun : program.methods()) {
        fun.entry().printSuccSets(outStream, nodes);
      }
      outStream.println();
      nodes = SmallSet.<CFGNode>empty().<CFGNode>mutable();
      for (CFGRoot fun : program.methods()) {
        fun.exit().printPredSets(outStream, nodes);
      }

      // graph.generatePDF(filename);
      // {
      //   ArrayList<String> cmdLd = new ArrayList<String>();
      //   cmdLd.add("python3");
      //   cmdLd.add("resources/pdf_merger.py");
      //   cmdLd.add(IJGraph.changeExtension(filename, ""));
      //   ProcessBuilder pb = new ProcessBuilder(cmdLd);
      //   Process process = pb.start();
      //   process.getOutputStream().close();
      //   process.waitFor();
      // }
      // ArrayList<String> cmdLd = new ArrayList<String>();
      // cmdLd.add("python3");
      // cmdLd.add("resources/deleteFiles.py");
      // cmdLd.add(IJGraph.changeExtension(filename, ""));
      // ProcessBuilder pb = new ProcessBuilder(cmdLd);
      // Process process = pb.start();
      // process.getOutputStream().close();
      // process.waitFor();
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