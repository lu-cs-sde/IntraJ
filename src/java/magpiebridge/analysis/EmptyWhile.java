package org.extendj.magpiebridge.analysis;

import java.util.Set;
import org.extendj.analysis.Warning;
import org.extendj.ast.CompilationUnit;
import org.extendj.magpiebridge.CodeAnalysis;
import org.eclipse.lsp4j.DiagnosticSeverity;

public class EmptyWhile extends CodeAnalysis {
  public String getName() { return "emptywhile"; }
  protected Set<Warning> getWarnings(CompilationUnit cu) { return cu.EMPTYWHILE(); }
  public DiagnosticSeverity getKind() { return DiagnosticSeverity.Warning; }
  @Override
  protected String reportKind(){return "codesmells";}
}
