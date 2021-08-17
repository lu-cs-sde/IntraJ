class NA16 {
  void m() {
    Integer p = null;
    try {
      p.toString();
    } catch (Throwable t) {
      // ignore
    }
  }
}