public class Catch04 {
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

  void m() throws TestException {}
}

class TestException extends RuntimeException {}
class TestSubException extends TestException {}