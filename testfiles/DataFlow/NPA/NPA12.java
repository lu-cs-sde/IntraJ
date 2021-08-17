class NA12 {
  void m() {
    Integer p = null;
    if (p != null) {
      p.toString();
      System.out.println("Test");
    }
    if (p == null) {
      p.toString();
      System.out.println("Test");
    }
  }
}