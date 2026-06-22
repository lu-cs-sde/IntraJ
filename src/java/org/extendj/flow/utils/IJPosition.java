package org.extendj.flow.utils;


import beaver.Symbol;
import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
import com.ibm.wala.classLoader.IMethod.SourcePosition;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import org.extendj.IntraJ;
import org.extendj.ast.ASTNode;

public class IJPosition implements Position {
  private int firstOffset;
  private int lastOffset;
  private int firstLine;
  private int lastLine;
  private int firstCol;
  private int lastCol;


  private String srcPath;

  public IJPosition(int firstLine, int lastLine, int firstCol, int lastCol,
                    String sourcePath) {
    this.firstOffset = Symbol.makePosition(firstLine, firstCol);
    this.lastOffset = Symbol.makePosition(lastLine, lastCol);

    this.firstLine = firstLine;
    this.lastLine = lastLine;

    this.firstCol = firstCol;
    this.lastCol = lastCol;
    this.srcPath = sourcePath;
  }

  @Override
  public int getFirstLine() {
    return firstLine;
  }

  @Override
  public int getLastLine() {
    return lastLine;
  }

  @Override
  public int getFirstCol() {
    return firstCol;
  }

  @Override
  public int getLastCol() {
    return lastCol;
  }

  @Override
  public int getFirstOffset() {
    return firstOffset;
  }

  @Override
  public int getLastOffset() {
    return lastOffset;
  }

  @Override
  public URL getURL() {
    try {
      return new URL("file:"+srcPath);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public int compareTo(SourcePosition o) {
    return o.getFirstOffset() - firstOffset;
  }

  @Override
  public Reader getReader() throws IOException {
    return new FileReader(new File(srcPath));
  }
}
