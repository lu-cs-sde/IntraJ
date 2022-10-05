package org.extendj.magpiebridge.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import org.extendj.magpiebridge.StaticServerAnalysis;
import org.extendj.magpiebridge.utility.FreeMarkerUtility;
import org.extendj.magpiebridge.utility.ProjectInfo;
import org.extendj.magpiebridge.utility.ResourceUtility;

public class RootHandler implements HttpHandler {

  private StaticServerAnalysis mpServer;
  private ProjectInfo projectInfo;
  public RootHandler(StaticServerAnalysis mpServer) {
    this.mpServer = mpServer;
    this.projectInfo = new ProjectInfo(mpServer);
  }

  private static final int HTTP_OK_STATUS = 200;
  @Override
  public void handle(HttpExchange t) throws IOException {

    String response = "";

    URI uri = t.getRequestURI();
    String path = uri.getPath();

    OutputStream os = t.getResponseBody();
    t.getResponseHeaders().add("Cache-Control",
                               "no-cache, no-store, must-revalidate");
    t.getResponseHeaders().add("Pragma", "no-cache");
    t.getResponseHeaders().add("Expires", "0");

    switch (path) {
    case "/index":
      response = FreeMarkerUtility.setFirstPageFile(projectInfo);
      t.sendResponseHeaders(HTTP_OK_STATUS, response.getBytes().length);
      os.write(response.getBytes());
      os.close();
      System.err.println("RootHandler: " + response);
      break;
    case "/static/app.css":
      response = ResourceUtility.getResourceAsString("static/app.css");
      System.err.println("RootHandler OK: " + response);
      t.sendResponseHeaders(HTTP_OK_STATUS, response.getBytes().length);
      os.write(response.getBytes());
      os.close();
      break;
    case "/img/logo.png":
      File imageFile = ResourceUtility.getResourceAsFile("static/logo.png");
      t.sendResponseHeaders(HTTP_OK_STATUS, imageFile.length());
      Files.copy(imageFile.toPath(), os);
      os.close();
      break;
    default:
      System.err.println("RootHandler default: " + path);
      break;
    }
  }
}