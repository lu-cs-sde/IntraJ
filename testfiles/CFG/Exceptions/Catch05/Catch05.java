public class Catch05 {
  void test() {
    try {
      try {
        m();
      } catch (TestSubException e) {
        m();
      }
    } catch (TestException e) {
      int a;
    }
    int b;
  }

  void m() throws TestException, TestSubException {}

  class TestException extends RuntimeException {}
  class TestSubException extends TestException {}
}