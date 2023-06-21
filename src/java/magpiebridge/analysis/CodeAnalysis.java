package org.extendj.magpiebridge;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.extendj.analysis.Warning;
import org.extendj.ast.CompilationUnit;
import org.extendj.magpiebridge.CodeAnalysis;
import org.extendj.magpiebridge.IJPosition;
import org.extendj.magpiebridge.Result;

public abstract class CodeAnalysis {
  private Collection<AnalysisResult> results = new HashSet<>();
  public void doAnalysis(CompilationUnit cu, URL url) {
    results.clear();
    for (Warning wm : getWarnings(cu)) {
      results.add(new Result(Kind.Diagnostic, wm.getPosition(), wm.getErrMsg(),
                             wm.getRelatedInfo(), getKind(), wm.getRepair(),
                             wm.getCode()));
     if(wm.getRepair()!=null){
      results.add(new Result(Kind.CodeLens, wm.getPosition(), wm.getErrMsg(),
                              wm.getRelatedInfo(), getKind(), wm.getRepair(),
                              wm.getCode()));
     }

     results.add(new Result(Kind.CodeLens, wm.getPosition(), wm.getErrMsg(),
                             wm.getRelatedInfo(), getKind(), null,
                             "https://idrissrio.github.io/intraj/docs/"+reportKind()+"/"+getName()));

    }
  }

  public Collection<AnalysisResult> getResult() { return results; }
  public abstract String getName();
  public DiagnosticSeverity getKind() { return DiagnosticSeverity.Error; }

  protected abstract Set<Warning> getWarnings(CompilationUnit cu);
  protected String reportKind(){return "checks";}

}
