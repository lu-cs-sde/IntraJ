public class Catch02 {
  void test() {
    try {
      try {
        m();
        int j;
      } catch (TestException e) {
        m();
      }
      int f;
    } catch (TestException e) {
      int c;
    }
    int d;
  }

  void m() throws TestException {}
}

class TestException extends RuntimeException {}
class TestSubException extends TestException {}
