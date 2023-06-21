package org.extendj.magpiebridge;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.Kind;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Command;
import java.util.ArrayList;

public class Result implements AnalysisResult {

  private final Kind kind;
  private final Position position;
  private final String message;
  private final Iterable<Pair<Position, String>> related;
  private final DiagnosticSeverity severity;
  private final Pair<Position, String> repair;
  private final String code;
  private final Command command=null;

  public Result(Kind kind, Position pos, String msg,
                Iterable<Pair<Position, String>> relatedInfo,
                DiagnosticSeverity severity, Pair<Position, String> repair,
                String code) {
    this.kind = kind;
    this.position = pos;
    this.message = msg;
    this.related = relatedInfo;
    this.severity = severity;
    this.repair = repair;
    this.code = code;
    // this.command = command;
  }

  public Kind kind() { return this.kind; }

  public Position position() { return position; }

  public Iterable<Pair<Position, String>> related() { return related; }

  public DiagnosticSeverity severity() { return severity; }

  public Pair<Position, String> repair() { return repair; }

  public String toString(boolean useMarkdown) { return message; }

  @Override // For Debugging
  public String toString() {
    return "Result [kind=" + kind + ", position=" + position +
        ", code=" + code + ", message=" + message + ", related=" + related +
        ", severity=" + severity + ", repair=" + repair + "]";
  }

  @Override
  public String code() {
    return code;
  }


}

