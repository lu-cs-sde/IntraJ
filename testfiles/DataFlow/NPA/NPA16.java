class NA16 {
  void m() {
    Integer p = null;
    try {
      p.toString(); // @NPA
    } catch (Throwable t) {
      // ignore
    }
  }
}