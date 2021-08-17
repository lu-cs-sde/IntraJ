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

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import org.extendj.IntraJ;
import org.extendj.ast.Analysis;
import org.extendj.ast.CFGNode;
import org.extendj.ast.CFGRoot;
import org.extendj.ast.CompilationUnit;
import org.extendj.ast.ConstructorDecl;
import org.extendj.ast.MethodDecl;
import org.extendj.ast.Program;
import org.extendj.ast.ReachedLVal;
import org.extendj.ast.WarningMsg;
import org.extendj.flow.utils.IJGraph;
import org.extendj.flow.utils.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

public class NPATests {
  /** Directory where the test input files are stored. */
  private static final File TEST_DIRECTORY = new File("testfiles/DataFlow/NPA");

  @Test
  public void NPA01() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA01.java", Analysis.NPA, 0);
  }

  @Test
  public void NPA02() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA02.java", Analysis.NPA, 0);
  }

  @Test
  public void NPA03() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA03.java", Analysis.NPA, 2);
  }

  @Test
  public void NPA04() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA04.java", Analysis.NPA, 1);
  }

  @Test
  public void NPA05() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA05.java", Analysis.NPA, 2);
  }

  @Test
  public void NPA06() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA06.java", Analysis.NPA, 0);
  }

  @Test
  public void NPA07() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA07.java", Analysis.NPA, 0);
  }

  @Test
  public void NPA08() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA08.java", Analysis.NPA, 0);
  }

  @Test
  public void NPA09() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA09.java", Analysis.NPA, 1);
  }

  @Test
  public void NPA10() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA10.java", Analysis.NPA, 1);
  }

  @Test
  public void NPA11() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA11.java", Analysis.NPA, 2);
  }

  @Test
  public void NPA12() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA12.java", Analysis.NPA, 1);
  }
  @Test
  public void NPA13() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA13.java", Analysis.NPA, 1);
  }

  @Test
  public void NPA14() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA14.java", Analysis.NPA, 0);
  }

  @Test
  public void NPA15() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA15.java", Analysis.NPA, 2);
  }

  @Test
  public void NPA16() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA16.java", Analysis.NPA, 1);
  }

  @Test
  public void NPA17() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA17.java", Analysis.NPA, 1);
  }

  @Test
  public void NPA18() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA18.java", Analysis.NPA, 0);
  }

  @Test
  public void NPA19() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA19.java", Analysis.NPA, 0);
  }

  @Test
  public void NPA20() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA20.java", Analysis.NPA, 1);
  }

  @Test
  public void NPA21() {
    UtilTest.checkWarnings(TEST_DIRECTORY, "NPA21.java", Analysis.NPA, 1);
  }
}
