package org.extendj.magpiebridge;

import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.SourceFileModule;
import java.io.*;
import java.io.File;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.net.*;
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
import org.extendj.magpiebridge.server.IntraJHttpServer;

public class StaticServerAnalysis implements ServerAnalysis {

  public Set<Path> srcPath = null;
  public Set<Path> libPath = null;
  public Set<Path> classPath = null;
  public Optional<Path> rootPath = null;
  public ExecutorService exeService;
  public List<Future<Void>> futures;
  private IntraJHttpServer httpServer;

  public IntraJ framework = IntraJ.getInstance();

  public Map<CodeAnalysis, Boolean> activeAnalyses;

  /**
   * Constructs a new instance of StaticServerAnalysis.
   * Initializes the executor service, active analyses, and other instance variables.
   */
  public StaticServerAnalysis() {
    exeService = Executors.newSingleThreadExecutor();
    activeAnalyses = initializeActiveAnalyses();
    futures = new ArrayList<>(activeAnalyses.size());
  }

  /**
   * Initializes the active analyses map with the desired code analyses.
   * @return The initialized active analyses map.
   */
  private Map<CodeAnalysis, Boolean> initializeActiveAnalyses() {
    Map<CodeAnalysis, Boolean> analyses = new LinkedHashMap<>();
    analyses.put(new StringEqAnalysis(), true);
    analyses.put(new DAAnalysis(), true);
    analyses.put(new NPAnalysis(), true);
    analyses.put(new IMPDAAnalysis(), true);
    return analyses;
  }

  @Override
  public String source() {return "IntraJ";
  }

  /**
   * Performs the analysis on the provided collection of files.
   * 
   * @param files    the collection of files to analyze
   * @param consumer the analysis consumer
   * @param rerun    specifies whether to rerun the analysis
   */
  @Override
  public void analyze(Collection<? extends Module> files, AnalysisConsumer consumer, boolean rerun) {
    MagpieServer server = (MagpieServer) consumer;
    JavaProjectService ps = (JavaProjectService) server.getProjectService("java").get();


    if (classPath == null || libPath == null || rootPath == null || srcPath == null) {
      retrievePaths(ps);
    }

    String classPathStr = computeClassPath(classPath, srcPath, libPath, rootPath);
    framework.setup(files, classPathStr);

    if (rerun) {
      doSingleAnalysisIteration(files, consumer);
    }
  }

  /**
   * Retrieves the paths (class path, library path, root path, source path) from the JavaProjectService.
   *
   * @param ps the JavaProjectService to retrieve the paths from
   */
  private void retrievePaths(JavaProjectService ps) {
    classPath = ps.getClassPath();
    libPath = ps.getLibraryPath();
    rootPath = ps.getRootPath();
    srcPath = ps.getSourcePath();
  }

  /**
   * Filters the given collection of modules to include only Java source files.
   *
   * @param files the collection of modules to filter
   * @return a collection of Java source file modules
   */
  private Collection<Module> filterJavaFiles(Collection<? extends Module> files) {
      ArrayList<Module> javaFiles = new ArrayList<>();
      for (Module file : files) {
          if (file instanceof SourceFileModule) {
              SourceFileModule sourceFileModule = (SourceFileModule) file;
              if(sourceFileModule.getURL().getPath().endsWith(".java")){
                  javaFiles.add(sourceFileModule);
              }
          }
      }
      return javaFiles;
  }


  /**
   * Performs a single analysis iteration on the provided collection of files.
   * 
   * @param files    the collection of files to analyze
   * @param consumer the analysis consumer
   * @return true if the analysis iteration was performed successfully, false otherwise
   */
  private boolean doSingleAnalysisIteration(Collection<? extends Module> files, AnalysisConsumer consumer) {
    MagpieServer server = (MagpieServer) consumer;
    
    // Construct the AST
    int exitCode = framework.run();
    System.err.println("IntraJ finished with exit code " + exitCode);
    
    // Clean up previous analysis results and ongoing analyses
    // server.cleanUp();
    cancelOngoingAnalyses(futures);
    
    // Initiate analyses on separate threads and set them running
    futures = executeAnalyses(files, server);
    
    return true;
  }

  /**
   * Cancels ongoing analysis threads.
   */
    private void cancelOngoingAnalyses(List<Future<Void>> futures) {
        for (Future<Void> future : futures) {
            future.cancel(true);
        }
    }


  /**
   * Executes the analyses on separate threads.
   * 
   * @param files    the collection of files to analyze
   * @param server   the MagpieServer instance
   */
    private List<Future<Void>> executeAnalyses(Collection<? extends Module> files, MagpieServer server) {
        List<Future<Void>> futures = new ArrayList<>();

        for (CodeAnalysis analysis : activeAnalyses.keySet()) {
            if (!activeAnalyses.get(analysis))
                continue;

            Future<Void> future = exeService.submit(() -> {
                doAnalysisThread(files, server, analysis);
                return null;
            });
            futures.add(future);
        }
        return futures;
    }

  /**
   * Performs the analysis on a single file using a specific analysis.
   * 
   * @param files     the collection of files to analyze
   * @param server    the MagpieServer instance
   * @param analysis  the analysis to perform
   */
  public void doAnalysisThread(Collection<? extends Module> files, MagpieServer server, CodeAnalysis analysis) {
    Collection<AnalysisResult> results = new ArrayList<>();
    for (Module file : files) {
      if (file instanceof SourceFileModule) {
        SourceFileModule sourceFile = (SourceFileModule) file;
        try {
          final URL clientURL = new URL(server.getClientUri(sourceFile.getURL().toString()));
          results.addAll(framework.analyze(sourceFile, clientURL, analysis));
        } catch (MalformedURLException e) {
          e.printStackTrace();
        }
      }
      server.consume(results, source());
    }
  }


    /**
   * Computes the class path string by combining the provided class paths, source paths,
   * library paths, and optional root path.
   *
   * @param classPath  the set of class paths
   * @param srcPath    the set of source paths
   * @param libPath    the set of library paths
   * @param rootPath   the optional root path
   * @return the computed class path string
   */
  static public String computeClassPath(Set<Path> classPath, Set<Path> srcPath, Set<Path> libPath, Optional<Path> rootPath) {
      StringBuilder sb = new StringBuilder();

      appendPathsToStringBuilder(sb, classPath);
      appendPathsToStringBuilder(sb, srcPath);
      appendPathsToStringBuilder(sb, libPath);

      if (rootPath.isPresent()) {
          try {
              Files.walkFileTree(rootPath.get(), new SimpleFileVisitor<Path>() {
                  @Override
                  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                          throws IOException {
                      if (file.toString().endsWith(".jar")) {
                          sb.append(file.toAbsolutePath().toString());
                          sb.append(":");
                      }
                      return FileVisitResult.CONTINUE;
                  }
              });
          } catch (IOException e) {
              e.printStackTrace();
              System.err.println("Error while iterating over the rootPath");
          }
      }


      return sb.toString();
  }

  /**
   * Appends the paths from the set to the provided StringBuilder.
   *
   * @param sb     the StringBuilder to append the paths to
   * @param paths  the set of paths to append
   */
  static private void appendPathsToStringBuilder(StringBuilder sb, Set<Path> paths) {
      for (Path path : paths) {
          sb.append(path.toAbsolutePath().toString());
          sb.append(":");
      }
  }

  @Override
  public void cleanUp(){
    futures.forEach(future -> future.cancel(true));
    exeService.shutdown();

  }


  @Override
  protected void finalize() throws Throwable {
    try {
      cleanUp();
      // Perform additional cleanup operations here
      // Write to a file or perform any necessary actions
    } finally {
      super.finalize();
    }
  }


}
