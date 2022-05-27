package org.extendj.magpiebridge;

import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.SourceFileModule;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import magpiebridge.core.AnalysisConsumer;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.IProjectService;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.ServerAnalysis;
import magpiebridge.core.analysis.configuration.ConfigurationOption;
import magpiebridge.core.analysis.configuration.OptionType;
import magpiebridge.projectservice.java.JavaProjectService;
import org.extendj.IntraJ;
import org.extendj.magpiebridge.CodeAnalysis;
import org.extendj.magpiebridge.analysis.*;

public class StaticServerAnalysis implements ServerAnalysis {

  public Set<String> srcPath;
  public Set<String> libPath;
  public Set<Path> classPath;

  public Set<String> progFilesAbsPaths;
  public Set<String> totalClassPath;
  public ExecutorService exeService;
  public Collection<Future<?>> last;

  public IntraJ framework = IntraJ.getInstance();

  public Map<CodeAnalysis, Boolean> activeAnalyses;

  public StaticServerAnalysis() {
    exeService = Executors.newSingleThreadExecutor();
    activeAnalyses = new LinkedHashMap<CodeAnalysis, Boolean>();
    activeAnalyses.put(new StringEqAnalysis(), true);
    activeAnalyses.put(new DAAnalysis(), true);
    activeAnalyses.put(new NPAnalysis(), true);
    progFilesAbsPaths = new HashSet<>();
    totalClassPath = new HashSet<>();
    last = new ArrayList<>(activeAnalyses.size());
  }

  @Override
  public String source() {
    return "IntraJ";
  }

  @Override
  public void analyze(Collection<? extends Module> files,
                      AnalysisConsumer consumer, boolean rerun) {
    if (rerun) {
      doSingleAnalysisIteration(files, consumer);
    }
  }

  private boolean doSingleAnalysisIteration(Collection<? extends Module> files,
                                            AnalysisConsumer consumer) {
    MagpieServer server = (MagpieServer)consumer;
    setClassPath(server, files);

    // Setup analysis framework and run
    framework.setup(files, totalClassPath, srcPath, libPath, progFilesAbsPaths);
    int exitCode = framework.run();

    // ANALYZE

    // Clean up previous analysis results and ongoing analyses
    server.cleanUp();
    for (Future<?> f : last) {
      if (f != null && !f.isDone()) {
        f.cancel(false);
      }
    }
    last.clear();

    // Initiate analyses on seperate threads and set them running
    for (CodeAnalysis analysis : activeAnalyses.keySet()) {
      if (!activeAnalyses.get(analysis))
        continue;
      last.add(exeService.submit(new Runnable() {
        @Override
        public void run() {
          doAnalysisThread(files, server, analysis);
        }
      }));
    }

    return true;
  }

  public void doAnalysisThread(Collection<? extends Module> files,
                               MagpieServer server, CodeAnalysis analysis) {
    Collection<AnalysisResult> results = new ArrayList<>();
    for (Module file : files) {
      if (file instanceof SourceFileModule) {
        SourceFileModule sourceFile = (SourceFileModule)file;
        try {
          final URL clientURL =
              new URL(server.getClientUri(sourceFile.getURL().toString()));
          results.addAll(framework.analyze(sourceFile, clientURL, analysis));
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      }
    }
    server.consume(results, source());
  }

  public void setClassPath(MagpieServer server,
                           Collection<? extends Module> files) {
    if (srcPath == null) {
      Optional<IProjectService> opt = server.getProjectService("java");
      if (opt.isPresent()) {
        JavaProjectService ps =
            (JavaProjectService)server.getProjectService("java").get();
        Set<Path> sourcePath = ps.getSourcePath();

        if (libPath == null) {
          libPath = new HashSet<>();
          ps.getLibraryPath().stream().forEach(
              path -> libPath.add(path.toString()));
        }
        if (!sourcePath.isEmpty()) {
          Set<String> temp = new HashSet<>();
          sourcePath.stream().forEach(path -> temp.add(path.toString()));
          srcPath = temp;
        }

        classPath = ps.getClassPath();
      }
    }

    updatePaths(files);
  }

  private void updatePaths(Collection<? extends Module> files) {
    progFilesAbsPaths.clear();
    totalClassPath.clear();
    totalClassPath.add(".");

    Set<String> requestedFiles = new HashSet<>();
    for (Module file : files) {
      if (file instanceof SourceFileModule) {
        SourceFileModule sourceFile = (SourceFileModule)file;
        progFilesAbsPaths.add(sourceFile.getAbsolutePath());
        requestedFiles.add(sourceFile.getClassName() + ".java");
      }
    }

    if (srcPath != null) {
      Iterator<String> srcIt = srcPath.iterator();

      // BUILD FROM SRC
      while (srcIt.hasNext()) {
        String src = srcIt.next();
        Collection<String> srcJavas =
            getJavaFilesForFolder(new File(src), ".java");
        for (String javaPath : srcJavas) {
          if (!requestedFiles.contains(getFileNameFromPath(javaPath)) &&
              !progFilesAbsPaths.contains(javaPath))
            progFilesAbsPaths.add(javaPath);
        }

        Collection<String> srcJars =
            getJavaFilesForFolder(new File(src), ".jar");
        for (String jarPath : srcJars) {
          totalClassPath.add(jarPath);
        }
      }
    }

    if (!libPath.isEmpty()) {
      Iterator<String> libIt = libPath.iterator();
      while (libIt.hasNext()) {
        String lib = libIt.next();
        Set<String> libJars =
            new HashSet<>(getJavaFilesForFolder(new File(lib), ".jar"));
        for (String jarPath : libJars) {
          totalClassPath.add(jarPath);
        }

        Collection<String> libJavas =
            getJavaFilesForFolder(new File(lib), ".java");
        for (String javaPath : libJavas) {
          if (!requestedFiles.contains(getFileNameFromPath(javaPath)) &&
              !progFilesAbsPaths.contains(javaPath))
            progFilesAbsPaths.add(javaPath);
        }
      }
    }

    for (Path p : classPath) {
      totalClassPath.add(p.toString());
    }
  }

  @Override
  public List<ConfigurationOption> getConfigurationOptions() {
    List<ConfigurationOption> options = new ArrayList<>();
    ConfigurationOption analyses =
        new ConfigurationOption("Analyses", OptionType.container);
    for (CodeAnalysis a : activeAnalyses.keySet()) {
      analyses.addChild(
          new ConfigurationOption(a.getName(), OptionType.checkbox, "true"));
    }
    options.add(analyses);
    return options;
  }

  @Override
  public void configure(List<ConfigurationOption> configuration) {
    for (ConfigurationOption o : configuration) {
      if (o.getName().equals("Analyses")) {
        for (ConfigurationOption c : o.getChildren()) {
          for (CodeAnalysis a : activeAnalyses.keySet()) {
            if (a.getName().equals(c.getName())) {
              activeAnalyses.put(a, c.getValueAsBoolean());
            }
          }
        }
      }
    }
  }

  // Helper methods for classpaths:
  public static Collection<String> getJavaFilesForFolder(final File folder,
                                                         String ext) {
    Collection<String> files = new HashSet<>();
    if (folder.isDirectory()) {
      for (final File fileEntry : folder.listFiles()) {
        if (fileEntry.isDirectory()) {
          files.addAll(getJavaFilesForFolder(fileEntry, ext));
        } else if (fileEntry.getName().endsWith(ext)) {
          files.add(fileEntry.getAbsolutePath());
        }
      }
    } else if (folder.getName().endsWith(ext)) {
      files.add(folder.getAbsolutePath());
    }

    return files;
  }

  public static String getFileNameFromPath(String path) {
    File f = new File(path);
    if (f.isFile())
      return f.getName();
    return "";
  }
}
