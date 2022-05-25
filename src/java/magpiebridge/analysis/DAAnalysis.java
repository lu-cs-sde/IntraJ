package org.extendj.magpiebridge.analysis;

import java.util.Set;
import org.extendj.ast.CompilationUnit;
import org.extendj.ast.WarningMsg;
import org.extendj.magpiebridge.CodeAnalysis;

public class DAAnalysis extends CodeAnalysis {
  public String getName() { return "DAAnalysis"; }
  protected Set<WarningMsg> getWarnings(CompilationUnit cu) { return cu.DAA(); }
}
