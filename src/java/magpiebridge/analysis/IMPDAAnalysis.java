package org.extendj.magpiebridge.analysis;

import java.util.Set;
import org.extendj.analysis.Warning;
import org.extendj.ast.CompilationUnit;
import org.extendj.magpiebridge.CodeAnalysis;

public class IMPDAAnalysis extends CodeAnalysis {
  public String getName() { return "IMPDAAnalysis"; }
  protected Set<Warning> getWarnings(CompilationUnit cu) { return cu.IMPDAA(); }
}
