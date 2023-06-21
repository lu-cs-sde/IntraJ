package org.extendj.magpiebridge.analysis;

import java.util.Set;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.extendj.analysis.Warning;
import org.extendj.ast.CompilationUnit;
import org.extendj.magpiebridge.CodeAnalysis;

public class IMPDAAnalysis extends CodeAnalysis {
  public String getName() { return "idaa"; }
  @Override
  public DiagnosticSeverity getKind() {
    return DiagnosticSeverity.Information;
  }
  protected Set<Warning> getWarnings(CompilationUnit cu) { return cu.IMPDAA(); }
}
