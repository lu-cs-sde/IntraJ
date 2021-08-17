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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeSet;
import org.extendj.IntraJ;
import org.extendj.ast.Analysis;
import org.extendj.ast.CFGNode;
import org.extendj.ast.CFGRoot;
import org.extendj.ast.CompilationUnit;
import org.extendj.ast.MethodDecl;
import org.extendj.ast.Program;
import org.extendj.ast.WarningMsg;
import org.extendj.flow.utils.IJGraph;
import org.extendj.flow.utils.Utils;

public class UtilTest {
  @SuppressWarnings("javadoc")
  public static Iterable<Object[]>
  getTestParametersSubFolders(File testDirectory, String extension) {
    Collection<Object[]> tests = new LinkedList<Object[]>();
    for (File f : testDirectory.listFiles()) {
      if (f.getName().endsWith(extension)) {
        tests.add(new Object[] {f.getPath()});
      }
      if (f.isDirectory()) {
        tests.addAll(
            (Collection<Object[]>)getTestParametersSubFolders(f, ".java"));
      }
    }
    return tests;
  }

  public static String changeExtension(String filename, String newExtension) {
    int index = filename.lastIndexOf('.');
    if (index != -1) {
      return filename.substring(0, index) + newExtension;
    } else {
      return filename + newExtension;
    }
  }

  public static void compareOutput(String actual, File out, File expected) {
    try {
      Files.write(out.toPath(), actual.getBytes());
      assertEquals("Output differs.", readFileToString(expected),
                   normalizeText(actual));
    } catch (IOException e) {
      fail("IOException occurred while comparing output: " + e.getMessage());
    }
  }

  private static String readFileToString(File file)
      throws FileNotFoundException {
    if (!file.isFile()) {
      return "";
    }

    Scanner scanner = new Scanner(file);
    scanner.useDelimiter("\\Z");
    String text = normalizeText(scanner.hasNext() ? scanner.next() : "");
    scanner.close();
    return text;
  }

  private static String normalizeText(String text) {
    return text.replace(SYS_LINE_SEP, "\n").trim();
  }
  private static String SYS_LINE_SEP = System.getProperty("line.separator");

  private static Program genAST(File file) {
    String[] filename = {file.getPath()};

    IntraJ jChecker = new IntraJ();
    int exitCode = jChecker.run(filename);
    if (exitCode != 0)
      fail("Compilation errors " + exitCode);

    return jChecker.getEntryPoint();
  }

  public static void checkWarnings(File file, String filename,
                                   Analysis analysis, int nerrors) {
    IntraJ.excludeLiteralsAndNull = true;
    Program program = genAST(new File(file, filename));
    int res = computeAnalysis(program, analysis);
    if (res != nerrors) {
      fail("Expected " + nerrors + " warnings. Got " + res + ".");
    }
  }

  private static int computeAnalysis(Program program, Analysis analysis) {
    Integer nbrWrn = 0;
    try {
      for (CompilationUnit cu : program.getCompilationUnits()) {
        TreeSet<WarningMsg> wmgs = (TreeSet<WarningMsg>)cu.getClass()
                                       .getDeclaredMethod(analysis.toString())
                                       .invoke(cu);
        for (WarningMsg wm : wmgs) {
          if (analysis.equals(wm.getAnalysisType())) {
            wm.print(System.out);
            nbrWrn++;
          }
        }
      }
    } catch (Throwable t) {
    }
    return nbrWrn;
  }
}