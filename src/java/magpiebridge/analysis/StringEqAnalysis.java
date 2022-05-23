package org.extendj.magpiebridge.analysis;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.extendj.ast.CompilationUnit;
import org.extendj.ast.Problem;
import org.extendj.ast.WarningMsg;
import org.extendj.magpiebridge.CodeAnalysis;
import org.extendj.magpiebridge.MySourceCodeReader;
import org.extendj.magpiebridge.Result;
import org.extendj.magpiebridge.ResultPosition;

public class StringEqAnalysis implements CodeAnalysis<CompilationUnit> {
  private Collection<AnalysisResult> results = new HashSet<>();

  public void doAnalysis(CompilationUnit cu, URL url) {
    results.clear();

    for (WarningMsg wm : cu.STREQ()) {

      String code = "No code";
      ResultPosition position = new ResultPosition(
          wm.lineStart, wm.lineEnd, wm.columnStart, wm.columnEnd, url);
      List<Pair<Position, String>> relatedInfo = new ArrayList<>();

      try {
        code = MySourceCodeReader.getLinesInString(position);
      } catch (Exception e) {
        e.printStackTrace();
      }

      String[] lr = code.split("==");

      for (int i = 0; i < lr.length; i++) {
        lr[i] = lr[i].trim();
        lr[i] = lr[i].replace("\n", "").replace("\r", "");
      }

      Pair<Position, String> repair = null;
      if (lr.length == 2) {
        String correctCode = lr[0] + ".equals(" + lr[1] + ")";
        repair = Pair.make(position, correctCode);
        relatedInfo.add(Pair.make(position, "Reason A"));
        relatedInfo.add(Pair.make(position, "Reason B"));
      }

      if (repair != null)
        relatedInfo.add(repair);

      // System.err.println("ATTENTION" + wm.relatedInfo);

      results.add(new Result(Kind.Diagnostic, position, wm.errMsg,
                             wm.relatedInfo, DiagnosticSeverity.Warning, repair,
                             code));
    }
  }

  public Collection<AnalysisResult> getResult() { return results; }

  @Override
  public String getName() {
    return "StringEqAnalysis";
  }

  public static String name() { return "StringEqAnalysis"; }
}
