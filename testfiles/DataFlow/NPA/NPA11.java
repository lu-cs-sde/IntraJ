class Connection {
  public Connection() throws NullPointerException {}

  void close() {}
}
class Statement {
  public Statement() throws NullPointerException {}
  void close() {}
}
class NA11 {
  void m() {
    Connection conn = null;
    Statement stmt = null;
    try {
      conn = new Connection();
      stmt = new Statement();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      stmt.close();
      conn.close();
    }
  }
}