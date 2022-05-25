package org.extendj.magpiebridge.analysis;

import java.util.Set;
import org.extendj.ast.CompilationUnit;
import org.extendj.ast.WarningMsg;
import org.extendj.magpiebridge.CodeAnalysis;

public class NPAnalysis extends CodeAnalysis {
  public String getName() { return "NPAnalysis"; }
  protected Set<WarningMsg> getWarnings(CompilationUnit cu) { return cu.NPA(); }
}
