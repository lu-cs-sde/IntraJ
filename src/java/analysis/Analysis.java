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
import java.io.IOException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class Analysis {

  // Enum that specifies all the available AvailableAnalysis
  public enum AvailableAnalysis {
    PURE,
    DAA,
    NPA,
    TEST;

    public static String[] names() {
      String valuesStr = Arrays.toString(AvailableAnalysis.values());
      return valuesStr.substring(1, valuesStr.length() - 1)
          .replace(" ", "")
          .split(",");
    }
  }


  private static Analysis analysis_instance = null;
  private static ArrayList<AvailableAnalysis> active_analysis =
      new ArrayList<AvailableAnalysis>();
  private static ArrayList<String> active_nodes = new ArrayList<String>() {
    {
      // ADD nodes here if you think that a node is always part of the CFG.
    }
  };
  public static Analysis getAnalysisInstance() {
    if (analysis_instance == null) {
      analysis_instance = new Analysis();
    }
    return analysis_instance;
  }

  public void addAnalysis(String analysisName) {
    if (Arrays.asList(AvailableAnalysis.names()).contains(analysisName)) {
      AvailableAnalysis analysis = AvailableAnalysis.valueOf(analysisName);
      active_analysis.add(analysis);
      readAnalysis(analysis);
    } else {
      System.out.println("Analysis " + analysisName + " not found");
      System.exit(1);
    }
  }

  public void addAnalysis(AvailableAnalysis analysis) {
    if(!analysis.toString().equals("TEST"))
    active_analysis.add(analysis);
    readAnalysis(analysis);
  }

  public void readAnalysis(AvailableAnalysis analysisName) {
    // Read active analysis from the Json file `ActiveNodes.json`
    File file = new File("src/java/analysis/ActiveNodes.json");
    try {
      JsonReader reader = new JsonReader(new FileReader(file));
      JsonParser parser = new JsonParser();
      JsonObject jsonObject = parser.parse(reader).getAsJsonObject();
      JsonArray jsonArray = jsonObject.getAsJsonArray(analysisName.toString());
      for (JsonElement jsonElement : jsonArray) {
        active_nodes.add(jsonElement.getAsString());
      }
      reader.close();
      
    } catch ( IOException e) {
      e.printStackTrace();
    }

  }

  public List<String> getActiveNodes() { return active_nodes; }
  public List<AvailableAnalysis> getActiveAnalyses() { return active_analysis; }
  public void enableAllAnalyses() {
    for (AvailableAnalysis analysis : AvailableAnalysis.values()) {
      active_analysis.add(analysis);
    }
  }

  public void disableAnalysis(String an) {
    // TODO: implement disableAnalysis
  }

  public void removeAllNodes() { active_nodes.clear(); }

  public void testing() {
    active_nodes.add("VarAccess");
    active_nodes.add("VariableDeclarator");
    active_nodes.add("AssignSimpleExpr");
    active_nodes.add("AssignMultiplicativeExpr");
    active_nodes.add("AssignAdditiveExpr");
    active_nodes.add("AssignShiftExpr");
    active_nodes.add("AssignBitwiseExpr");
    active_nodes.add("PostIncExpr");
    active_nodes.add("PreIncExpr");
    active_nodes.add("PreDecExpr");
    active_nodes.add("MethodAccess");
    active_nodes.add("VarAccess");
    active_nodes.add("VariableDeclarator");
    active_nodes.add("AssignMultiplicativeExpr");
    active_nodes.add("AssignAdditiveExpr");
    active_nodes.add("AssignShiftExpr");
    active_nodes.add("AssignBitwiseExpr");
    active_nodes.add("PostIncExpr");
    active_nodes.add("PostDecExpr");
    active_nodes.add("PreIncExpr");
    active_nodes.add("LTExpr");
    active_nodes.add("GTExpr");
    active_nodes.add("LEExpr");
    active_nodes.add("GEExpr");
    active_nodes.add("EQExpr");
    active_nodes.add("NEExpr");
    active_nodes.add("FloatingPointLiteral");
    active_nodes.add("DoubleLiteral");
    active_nodes.add("PreDecExpr");
    active_nodes.add("ImplicitAssignment");
    active_nodes.add("MethodAccess");
    active_nodes.add("ConstructorAccess");
    active_nodes.add("SuperConstructorAccess");
    active_nodes.add("ArrayAccess");
    active_nodes.add("ArrayTypeWithSizeAccess");
    active_nodes.add("ThisAccess");
    active_nodes.add("SuperAccess");
    active_nodes.add("ArrayAccess");
    active_nodes.add("ResourceDeclaration");
    active_nodes.add("FieldDeclarator");
    active_nodes.add("ReturnStmt");
    active_nodes.add("InstanceOfExpr");
    active_nodes.add("ClassInstanceExpr");
    active_nodes.add("ConstCase");
    active_nodes.add("DefaultCase");
    active_nodes.add("LocalClassDeclStmt");
    active_nodes.add("ThrowStmt");
    active_nodes.add("CatchClause");
    active_nodes.add("AssertStmt");
    active_nodes.add("UncheckedExceptions");
    active_nodes.add("CatchParameterDeclaration");
    active_nodes.add("EnumConstant");
    active_nodes.add("InferredParameterDeclaration"); // Java8
    active_nodes.add("ParameterDeclaration");
    active_nodes.add("BooleanLiteral");
    active_nodes.add("CharacterLiteral");
    active_nodes.add("DoubleLiteral");
    active_nodes.add("FloatingPointLiteral");
    active_nodes.add("IllegalLiteral");
    active_nodes.add("IntegerLiteral");
    active_nodes.add("LongLiteral");
    active_nodes.add("NullLiteral");
    active_nodes.add("StringLiteral");
    active_nodes.add("ContinueStmt");
    active_nodes.add("ParExpr");
    active_nodes.add("BitNotExpr");
    active_nodes.add("LogNotExpr");
    active_nodes.add("MinusExpr");
    active_nodes.add("PlusExpr");
    active_nodes.add("ImplicitCondition");
    active_nodes.add("MulExpr");
    active_nodes.add("DivExpr");
    active_nodes.add("ModExpr");
    active_nodes.add("AddExpr");
    active_nodes.add("SubExpr");
    active_nodes.add("LShiftExpr");
    active_nodes.add("RShiftExpr");
    active_nodes.add("URShiftExpr");
    active_nodes.add("AndBitwiseExpr");
    active_nodes.add("OrBitwiseExpr");
    active_nodes.add("XorBitwiseExpr");
    active_nodes.add("AndLogicalExpr");
    active_nodes.add("OrLogicalExpr");
    active_nodes.add("Implicit");
    active_nodes.add("ArrayCreationExpr");
    active_nodes.add("TypeAccess");
    active_nodes.add("PackageAccess");
  }
}
