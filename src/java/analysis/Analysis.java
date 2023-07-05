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

package org.extendj.analysis;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Analysis {

  // Enum that specifies all the available AvailableAnalysis
  public enum AvailableAnalysis {
    DAA,
    AUMG,
    STREQ,
    IMPDAA,
    EMPTYIF,
    EMPTYWHILE,
    SWITCHDEFAULT,
    NPA;

    // CFG;

    public static String[] names() {
      String valuesStr = Arrays.toString(AvailableAnalysis.values());
      return valuesStr.substring(1, valuesStr.length() - 1)
          .replace(" ", "")
          .split(",");
    }

  }

  private static Analysis analysis_instance = null;
  private ArrayList<AvailableAnalysis> active_analysis =
      new ArrayList<AvailableAnalysis>();

  public static Analysis getAnalysisInstance() {
    if (analysis_instance == null) {
      analysis_instance = new Analysis();
    }
    return analysis_instance;
  }

  public void addAnalysis(AvailableAnalysis analysis) {
    active_analysis.add(analysis);
  }

  public AvailableAnalysis getAnalysis(String analysisName) {
    if (Arrays.asList(AvailableAnalysis.names()).contains(analysisName)) {
      AvailableAnalysis analysis = AvailableAnalysis.valueOf(analysisName);
      return analysis;
    } else {
      throw new RuntimeException("Analysis " + analysisName + " not found");
    }
  }

  public List<AvailableAnalysis> getActiveAnalyses() { return active_analysis; }
  public void enableAllAnalyses() {
    for (AvailableAnalysis analysis : AvailableAnalysis.values()) {
      active_analysis.add(analysis);
    }
  }

  public void disableAnalysis(AvailableAnalysis analysis) {
    active_analysis.remove(analysis);
  }
}

