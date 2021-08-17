public class Catch06 {
  void test() {
    try {
      try {
        m();
      } catch (TestException1 e) {
        m();
      }
    } catch (TestException2 e) {
      int a;
    }
    int b;
  }

  void m() throws TestException1, TestException2 {}

  class TestException1 extends RuntimeException {}
  class TestException2 extends RuntimeException {}
}