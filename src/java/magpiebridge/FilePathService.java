package org.extendj.magpiebridge;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

public class FilePathService {

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
