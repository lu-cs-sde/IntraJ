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

public class IntraJFramework extends AnalysisFramework {
  private IntraJ jChecker;
  private Collection<String> args;

  @Override
  public void setup(Collection<? extends Module> files, Set<String> classPath,
                    Set<String> srcPath, Set<String> libPath,
                    Set<String> progPath) {
    jChecker = new IntraJ();
    args = new LinkedHashSet<String>();

    args.add("-nowarn");

    if (!classPath.isEmpty()) {
      args.add("-classpath");
      args.add(calculateClassPathString(classPath));
    }

    for (String path : progPath) {
      args.add(path);
    }
  }

  @Override
  public int run() {
    return jChecker.run(args.toArray(new String[args.size()]));
  }

  @Override
  public Collection<AnalysisResult>
  analyze(SourceFileModule file, URL clientURL, CodeAnalysis analysis) {
    for (CompilationUnit cu : jChecker.getEntryPoint().getCompilationUnits()) {
      if (cu.getClassSource().sourceName().equals(file.getAbsolutePath())) {
        analysis.doAnalysis(cu, clientURL);
      }
    }

    return analysis.getResult();
  }

  @Override
  public String frameworkName() {
    return "IntraJ";
  }
}
