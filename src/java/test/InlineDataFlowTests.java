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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Parameterized test that auto-discovers dataflow analysis test files
 * under {@code testfiles/DataFlow/} and verifies warnings against
 * inline {@code // @ANALYSIS} annotations.
 *
 * <p>Each subdirectory name (NPA, DAA, IMPDAA, ...) serves as the
 * default analysis type. Expected warnings are declared directly in
 * the test source files using end-of-line comments:
 * <pre>
 *   a.toString(); // @NPA
 *   int x = 0;    // @DAA
 * </pre>
 *
 * <p>Files with no annotations are tested for zero warnings under
 * the directory's default analysis.
 */
@RunWith(Parameterized.class)
public class InlineDataFlowTests {

  private static final File DATA_FLOW_ROOT =
      new File("testfiles/DataFlow");

  private final File testFile;
  private final String defaultAnalysis;

  public InlineDataFlowTests(String filePath, String analysis) {
    this.testFile = new File(filePath);
    this.defaultAnalysis = analysis;
  }

  @Parameters(name = "{1}: {0}")
  public static Collection<Object[]> data() {
    List<Object[]> params = new ArrayList<>();
    File[] subdirs = DATA_FLOW_ROOT.listFiles(File::isDirectory);
    if (subdirs == null) return params;
    for (File subdir : subdirs) {
      String analysisName = subdir.getName();
      collectTests(subdir, analysisName, params);
    }
    return params;
  }

  private static void collectTests(File dir, String analysisName,
                                   List<Object[]> params) {
    File[] files = dir.listFiles();
    if (files == null) return;
    for (File f : files) {
      if (f.isFile() && f.getName().endsWith(".java")) {
        params.add(new Object[]{f.getPath(), analysisName});
      } else if (f.isDirectory()) {
        collectTests(f, analysisName, params);
      }
    }
  }

  @Test
  public void testWarnings() {
    UtilTest.checkWarningsInline(testFile, defaultAnalysis);
  }
}
