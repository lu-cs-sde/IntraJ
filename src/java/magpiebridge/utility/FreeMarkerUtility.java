package org.extendj.magpiebridge.utility;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FreeMarkerUtility {
  public static String setFirstPageFile(ProjectInfo info) {

    String resolvedProjectName = "null";

    // if (projectName != null && !"".equals(projectName))
    //   resolvedProjectName = projectName;

    Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
    StringWriter out = new StringWriter();
    try {
      cfg.setClassForTemplateLoading(FreeMarkerUtility.class, "/");
      cfg.setDefaultEncoding("UTF-8");

      Template template = cfg.getTemplate("home.ftl");

      Map<String, Object> templateData = new HashMap<>();
      if (info.getClassPath() == null) {
        System.err.println("Classpath is null");
      }

      templateData.put("projectName", info.getProjectName());
      templateData.put("numCFG", info.getNumCFG());
      templateData.put("rootPath", info.getRootPath());
      templateData.put("libPath", info.getLibPath());
      templateData.put("classPath", info.getClassPath());
      templateData.put("labels", info.getLabels());
      templateData.put("data", info.getProjectFetureSummary());
      templateData.put("faetures", info.getFaeturesAsJSON());
      templateData.put("barChartLabels", info.getFeatureLabels());
      for (int i = 0; i < 8; i++) {
        templateData.put("barChartData" + (i + 1),
                         info.getFeatureData("JAVA" + (i + 1)));
      }

      template.process(templateData, out);
      out.flush();

      return out.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "Error";
  }
}