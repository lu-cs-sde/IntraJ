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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import magpiebridge.core.AnalysisConsumer;
import magpiebridge.core.AnalysisResult;
import magpiebridge.core.IProjectService;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.ServerAnalysis;
import magpiebridge.core.analysis.configuration.ConfigurationOption;
import magpiebridge.core.analysis.configuration.OptionType;
import magpiebridge.projectservice.java.JavaProjectService;
import org.extendj.magpiebridge.AnalysisFramework;
import org.extendj.magpiebridge.CodeAnalysis;
import org.extendj.magpiebridge.FilePathService;
import org.extendj.magpiebridge.IntraJFramework;

public class StaticServerAnalysis implements ServerAnalysis {
  private static final Logger LOG = Logger.getLogger("main");
  public Set<String> srcPath;
  public Set<String> libPath;
  public Set<Path> classPath;

  public Set<String> progFilesAbsPaths;
  public Set<String> totalClassPath;
  public ExecutorService exeService;
  public Collection<Future<?>> last;

  public AnalysisFramework framework;

  public static Map<String, Boolean> activeAnalyses;
  public static Collection<CodeAnalysis<?>> analysisList;

  public StaticServerAnalysis() {
    exeService = Executors.newSingleThreadExecutor();
    analysisList = new ArrayList<CodeAnalysis<?>>();
    progFilesAbsPaths = new HashSet<>();
    totalClassPath = new HashSet<>();
    last = new ArrayList<>(analysisList.size());
    activeAnalyses = new HashMap<>();

    framework = new IntraJFramework();
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

    LOG.info(framework.frameworkName() + " run completed with exitCode " +
             exitCode);

    // ANALYZE

    // Clean up previous analysis results and ongoing analyses
    server.cleanUp();
    for (Future<?> f : last) {
      if (f != null && !f.isDone()) {
        f.cancel(false);
        if (f.isCancelled())
          LOG.info("Susscessfully cancelled last analysis and start new");
      }
    }
    last.clear();

    // Initiate analyses on seperate threads and set them running
    for (CodeAnalysis analysis : analysisList) {
      if (!activeAnalyses.get(analysis.getName()))
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
    Collection<AnalysisResult> results =
        new ArrayList<>(Collections.emptyList());
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
    LOG.severe("setClassPath");
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

  public static void addAnalysis(CodeAnalysis<?> analysis) {

    analysisList.add(analysis);
    activeAnalyses.put(analysis.getName(), true);
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
            FilePathService.getJavaFilesForFolder(new File(src), ".java");
        for (String javaPath : srcJavas) {
          if (!requestedFiles.contains(
                  FilePathService.getFileNameFromPath(javaPath)) &&
              !progFilesAbsPaths.contains(javaPath))
            progFilesAbsPaths.add(javaPath);
        }

        Collection<String> srcJars =
            FilePathService.getJavaFilesForFolder(new File(src), ".jar");
        for (String jarPath : srcJars) {
          totalClassPath.add(jarPath);
        }
      }
    }

    if (!libPath.isEmpty()) {
      Iterator<String> libIt = libPath.iterator();
      while (libIt.hasNext()) {
        String lib = libIt.next();
        Set<String> libJars = new HashSet<>(
            FilePathService.getJavaFilesForFolder(new File(lib), ".jar"));
        for (String jarPath : libJars) {
          totalClassPath.add(jarPath);
        }

        Collection<String> libJavas =
            FilePathService.getJavaFilesForFolder(new File(lib), ".java");
        for (String javaPath : libJavas) {
          if (!requestedFiles.contains(
                  FilePathService.getFileNameFromPath(javaPath)) &&
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
    LOG.severe("getConfigurationOptions");
    List<ConfigurationOption> options = new ArrayList<>();
    ConfigurationOption framework =
        new ConfigurationOption("Frameworks", OptionType.container);
    framework.addChild(
        new ConfigurationOption("IntraJ", OptionType.checkbox, "true"));

    ConfigurationOption analyses =
        new ConfigurationOption("Analyses", OptionType.container);

    for (CodeAnalysis a : analysisList) {
      analyses.addChild(
          new ConfigurationOption(a.getName(), OptionType.checkbox, "true"));
    }

    options.add(framework);
    options.add(analyses);
    return options;
  }

  @Override
  public void configure(List<ConfigurationOption> configuration) {
    LOG.severe("configure");
    for (ConfigurationOption o : configuration) {
      switch (o.getName()) {
      case "Frameworks":
        for (ConfigurationOption c : o.getChildren()) {
          if (c.getValueAsBoolean())
            framework = frameworkConstructor(c.getName());
        }
        break;
      case "Analyses":
        for (ConfigurationOption c : o.getChildren()) {
          activeAnalyses.put(c.getName(), c.getValueAsBoolean());
        }
        break;
      default:
        LOG.warning("No configuration case mathcing " + o.getName());
      }
    }
  }

  private AnalysisFramework frameworkConstructor(String name) {
    LOG.severe("frameworkConstructor");
    switch (name) {
    case "IntraJ":
      return new IntraJFramework();
    default:
      LOG.severe("No framework matched name " + name);
      return null;
    }
  }
}
