package org.extendj.magpiebridge.analysis;

import java.util.Set;
import org.extendj.analysis.Warning;
import org.extendj.ast.CompilationUnit;
import org.extendj.magpiebridge.CodeAnalysis;
import org.eclipse.lsp4j.DiagnosticSeverity;

public class EmptyIf extends CodeAnalysis {
  public String getName() { return "emptyif"; }
  protected Set<Warning> getWarnings(CompilationUnit cu) { return cu.EMPTYIF(); }
  public DiagnosticSeverity getKind() { return DiagnosticSeverity.Warning; }
  @Override
  protected String reportKind(){return "codesmells";}
}
