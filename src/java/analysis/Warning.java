package org.extendj.analysis;

import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.util.collections.Pair;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import magpiebridge.util.SourceCodeReader;
import org.extendj.IntraJ;
import org.extendj.flow.utils.Utils;
import org.extendj.magpiebridge.ResultPosition;

public class Warning implements Comparable<Warning> {
  private final Integer lineStart, columnStart, lineEnd, columnEnd;
  private final String errMsg;
  final Analysis.AvailableAnalysis analysis;
  final String sourceFilePath;
  private final Pair<Position, String> repair;
  private final java.util.List<Pair<Position, String>> relatedInfo;
  private ResultPosition position = null;
  private String code = "Error...";

  public Warning(String sourceFilePath, Analysis.AvailableAnalysis analysis,
                 Integer lineStart, Integer columnStart, Integer lineEnd,
                 Integer columnEnd, String errMsg,
                 Pair<Position, String> repair,
                 java.util.List<Pair<Position, String>> relatedInfo) {
    this.analysis = analysis;
    this.lineStart = lineStart;
    this.lineEnd = lineEnd;
    this.errMsg = errMsg;
    this.sourceFilePath = sourceFilePath;
    this.columnEnd = columnEnd;
    this.columnStart = columnStart;
    this.repair = repair;
    this.relatedInfo = relatedInfo;
    try {
      if (IntraJ.vscode) {
        URL clientURL = new URL(IntraJ.server.getClientUri(
            new File(sourceFilePath).toURI().toString()));
        this.position = new ResultPosition(lineStart, lineEnd, columnStart,
                                           columnEnd, clientURL);
        this.code = SourceCodeReader.getLinesInString(position);
      }
    } catch (Exception e) {

      e.printStackTrace();
    }
  }

  public int compareTo(Warning other) {
    if (lineStart.equals(other.lineStart)) {
      if (columnStart != null) {
        if (columnStart.equals(other.columnStart))
          return errMsg.compareTo(other.errMsg);
        else {
          if (columnStart != null && other.columnStart != null)
            return Integer.compare(columnStart, other.columnStart);
          else {
            if (columnStart == null) {
              return -1;
            } else {
              return 1;
            }
          }
        }
      } else {
        return Integer.compare(columnEnd, other.columnEnd);
      }
    } else {
      return Integer.compare(lineStart, other.lineStart);
    }
  }

  public Integer getLineStart() { return lineStart; }

  public Integer getColumnStart() { return columnStart; }

  public Integer getLineEnd() { return lineEnd; }

  public Integer getColumnEnd() { return columnEnd; }

  public String getErrMsg() { return errMsg; }

  public Pair<Position, String> getRepair() { return repair; }

  public java.util.List<Pair<Position, String>> getRelatedInfo() {
    return relatedInfo;
  }

  public ResultPosition getPosition() { return position; }

  public String getCode() { return code; }

  public String getSourceFilePath() { return sourceFilePath; }

  public void print(PrintStream out) {
    Utils.printWarning(out,
                       analysis.name() + " - " + sourceFilePath + ":" +
                           lineStart + "," + columnStart,
                       errMsg);
  }

  public String toString() {
    return analysis.name() + " - " + sourceFilePath + ":" + lineStart + "," +
        columnStart + " - " + errMsg;
  }

  public Analysis.AvailableAnalysis getAnalysisType() { return analysis; }
}
