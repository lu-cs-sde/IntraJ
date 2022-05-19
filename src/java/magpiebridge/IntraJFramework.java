package org.extendj.magpiebridge;

import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.SourceFileModule;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import magpiebridge.core.AnalysisResult;
import org.extendj.IntraJ;
import org.extendj.ast.CompilationUnit;

public class IntraJFramework {
  private IntraJ jChecker;
  private Collection<String> args;

  public void setup(Collection<? extends Module> files, Set<String> classPath,
                    Set<String> srcPath, Set<String> libPath,
                    Set<String> progPath) {
    jChecker = IntraJ.getInstance();
    args = new LinkedHashSet<String>();

    args.add("-nowarn");

    if (!classPath.isEmpty()) {
      args.add("-classpath");
      args.add(computeClassPath(classPath));
    }

    for (String path : progPath) {
      args.add(path);
    }
  }

  public int run() {
    return jChecker.run(args.toArray(new String[args.size()]));
  }

  public Collection<AnalysisResult>
  analyze(SourceFileModule file, URL clientURL, CodeAnalysis analysis) {
    for (CompilationUnit cu : jChecker.getEntryPoint().getCompilationUnits()) {
      if (cu.getClassSource().sourceName().equals(file.getAbsolutePath())) {
        analysis.doAnalysis(cu, clientURL);
      }
    }

    return analysis.getResult();
  }

  public String frameworkName() { return "IntraJ"; }

  protected String computeClassPath(Set<String> classPath) {
    StringBuilder sb = new StringBuilder();
    for (String path : classPath) {
      sb.append(path);
      sb.append(";");
    }

    return sb.toString();
  }
}
