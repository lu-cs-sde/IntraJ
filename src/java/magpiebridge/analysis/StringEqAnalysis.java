package org.extendj.magpiebridge.analysis;

import java.util.Set;
import org.extendj.ast.CompilationUnit;
import org.extendj.ast.WarningMsg;
import org.extendj.magpiebridge.CodeAnalysis;

public class StringEqAnalysis extends CodeAnalysis {
  protected Set<WarningMsg> getWarnings(CompilationUnit cu) {
    return cu.STREQ();
  }

  public String getName() { return "StringEqAnalysis"; }
}
