package org.extendj.magpiebridge.utility;
import java.nio.file.Path;
import java.util.*;
import java.util.Optional;
import java.util.Set;
import org.extendj.feature.Feature;
import org.extendj.magpiebridge.StaticServerAnalysis;

public class ProjectInfo {
  private StaticServerAnalysis mpServer;

  public ProjectInfo(StaticServerAnalysis mpServer) {
    this.mpServer = mpServer;
  }

  public int getNumCFG() {

    try {
      return mpServer.framework.getEntryPoint().CFGRoots().size();
    } catch (Throwable t) {
      return 0;
    }
  }

  public String getProjectFetureSummary() {
    Set<Feature> features = mpServer.framework.getEntryPoint().features();
    // Count occurrences of JAVA 1,2,3,4, 5, 7, 8
    int[] counts = new int[8];
    for (Feature f : features) {
      if (f.getCluster().equals("JAVA1")) {
        counts[0]++;
      } else if (f.getCluster().equals("JAVA2")) {
        counts[1]++;
      } else if (f.getCluster().equals("JAVA3")) {
        counts[2]++;
      } else if (f.getCluster().equals("JAVA4")) {
        counts[3]++;
      } else if (f.getCluster().equals("JAVA5")) {
        counts[4]++;
      } else if (f.getCluster().equals("JAVA6")) {
        counts[5]++;
      } else if (f.getCluster().equals("JAVA7")) {
        counts[6]++;
      } else if (f.getCluster().equals("JAVA8")) {
        counts[7]++;
      }
    }
    String result = "[";
    for (int i = 0; i < counts.length; i++) {
      result += counts[i];
      if (i < counts.length - 1) {
        result += ",";
      }
    }
    result += "]";
    System.err.println("Project feature summary: " + result);
    return result;
  }

  public String getLibPath() {
    if (mpServer.libPath == null) {
      return "[]";
    } else {
      return prettyPrint(mpServer.libPath);
    }
  }

  public String getClassPath() {
    if (mpServer.classPath == null) {
      return "[]";
    } else {
      return prettyPrint(mpServer.classPath);
    }
  }

  public String getRootPath() {
    if (mpServer.rootPath == null) {
      return "Empty";
    }
    String path = mpServer.rootPath.get().toString();
    String res = path.substring(path.lastIndexOf("/") + 1, path.length());
    return path;
  }

  public String getProjectName() {
    if (mpServer.rootPath == null) {
      return "Not able to resolve project name";
    }
    String path = mpServer.rootPath.get().toString();
    String res = path.substring(path.lastIndexOf("/") + 1, path.length());
    return res;
  }

  private String prettyPrint(Set<Path> paths) {
    String pretty = "";
    for (Path path : paths) {
      pretty += path.toString() + System.lineSeparator();
    }
    return pretty;
  }

  public String getLabels() {
    return "[ 'Java 1', 'Java 2', 'Java 3', 'Java 4', 'Java 5', 'Java 6', 'Java 7', 'Java 8']";
  }

  public List<Map.Entry<String, Set<Feature>>> getFeatureSummary() {
    HashMap<String, Set<Feature>> featureMap =
        new HashMap<String, Set<Feature>>();
    Set<Feature> features = null;
    try {
      features = mpServer.framework.getEntryPoint().features();
    } catch (Throwable t) {
      System.err.println("Error: " + t.getMessage());
      return new ArrayList<Map.Entry<String, Set<Feature>>>();
    }
    for (Feature f : features) {
      if (featureMap.containsKey(f.getFeatureID())) {
        featureMap.get(f.getFeatureID()).add(f);
      } else {
        Set<Feature> newSet = new HashSet<Feature>();
        newSet.add(f);
        featureMap.put(f.getFeatureID(), newSet);
      }
    }
    List<Map.Entry<String, Set<Feature>>> list =
        new LinkedList<Map.Entry<String, Set<Feature>>>(featureMap.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<String, Set<Feature>>>() {
      public int compare(Map.Entry<String, Set<Feature>> o1,
                         Map.Entry<String, Set<Feature>> o2) {
        return (o1.getValue().size() - o2.getValue().size());
      }
    });
    return list;
  }

  public String getFeatureData(String cluster) {
    List<Map.Entry<String, Set<Feature>>> list = getFeatureSummary();
    String result = "[";
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).getValue().iterator().next().getCluster().equals(
              cluster)) {
        result += list.get(i).getValue().size();
      }
      if (i < list.size() - 1) {
        result += ",";
      }
    }

    result += "]";
    return result;
  }

  public String getFeatureLabels() {
    List<Map.Entry<String, Set<Feature>>> list = getFeatureSummary();
    String result = "[";
    for (int i = 0; i < list.size(); i++) {
      result += "'" + list.get(i).getKey() + "'";
      if (i < list.size() - 1) {
        result += ",";
      }
    }
    result += "]";

    return result;
  }
}