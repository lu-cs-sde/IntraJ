package org.extendj.magpiebridge;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;
import magpiebridge.util.SourceCodeReader;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.extendj.ast.Analysis;
import org.extendj.ast.CompilationUnit;
import org.extendj.ast.WarningMsg;
import org.extendj.magpiebridge.CodeAnalysis;
import org.extendj.magpiebridge.MySourceCodeReader;
import org.extendj.magpiebridge.Result;
import org.extendj.magpiebridge.ResultPosition;

public abstract class CodeAnalysis {
  private Collection<AnalysisResult> results = new HashSet<>();
  public void doAnalysis(CompilationUnit cu, URL url) {
    results.clear();
    for (WarningMsg wm : getWarnings(cu)) {

      ResultPosition position = new ResultPosition(
          wm.lineStart, wm.lineEnd, wm.columnStart, wm.columnEnd, url);
      String code = "Error...";
      try {
        code = SourceCodeReader.getLinesInString(position);
      } catch (Exception e) {
        e.printStackTrace();
      }

      results.add(new Result(Kind.Diagnostic, position, wm.errMsg,
                             wm.relatedInfo, DiagnosticSeverity.Warning,
                             wm.repair, code));
    }
  }

  public Collection<AnalysisResult> getResult() { return results; }
  public abstract String getName();

  protected abstract Set<WarningMsg> getWarnings(CompilationUnit cu);
}
