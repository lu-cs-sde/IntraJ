package org.extendj.magpiebridge;

import java.net.URL;
import java.util.Collection;
import magpiebridge.core.AnalysisResult;

public interface CodeAnalysis<T> {

  public void doAnalysis(T cu, URL url);

  public Collection<AnalysisResult> getResult();

  public String getName();
}
