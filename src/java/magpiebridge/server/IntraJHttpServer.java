package org.extendj.magpiebridge.server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.extendj.magpiebridge.StaticServerAnalysis;

@SuppressWarnings("restriction")
public class IntraJHttpServer {

  private HttpServer httpServer;
  private RootHandler rootHandler;

  public IntraJHttpServer(StaticServerAnalysis mpServer) {
    rootHandler = new RootHandler(mpServer);
    try {
      httpServer = HttpServer.create(new InetSocketAddress(0), 0);
      httpServer.createContext("/index", rootHandler);
      httpServer.createContext("/static/app.css", rootHandler);
      httpServer.createContext("/img/logo.png", rootHandler);
      httpServer.createContext("/imgs/script.js", rootHandler);
      httpServer.setExecutor(null);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public HttpServer getServerInstance() { return httpServer; }

  public String start() {
    this.httpServer.start();
    return "http://localhost:" + this.httpServer.getAddress().getPort() +
        "/index";
  }
}