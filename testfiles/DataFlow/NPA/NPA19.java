class NA19 {
  void test() {
    Integer parser = null;

    if (parser == null) {
      throw new RuntimeException("error");
    }

    try {
      parser.toString();
    } catch (Exception e) {
    }

    parser.toString();
  }
}