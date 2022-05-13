package org.extendj.magpiebridge;

import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.SourceFileModule;
import java.net.URL;
import java.util.Collection;
import java.util.Set;
import magpiebridge.core.AnalysisResult;

public abstract class AnalysisFramework {
  public abstract void setup(Collection<? extends Module> files,
                             Set<String> classPath, Set<String> srcPath,
                             Set<String> libPath, Set<String> progPath);

  public abstract int run();

  public abstract Collection<AnalysisResult>
  analyze(SourceFileModule file, URL clientURL, CodeAnalysis analysis);

  public abstract String frameworkName();

  protected String calculateClassPathString(Set<String> classPath) {
    StringBuilder sb = new StringBuilder();
    for (String path : classPath) {
      sb.append(path);
      sb.append(";");
    }

    return sb.toString();
  }
}
