public class Catch01 {
  void test() {
    try {
      m();
    } catch (TestSubException e) {
      m();
    }
  }

  void m() throws TestException {}
}

class TestException extends RuntimeException {}
class TestSubException extends TestException {}
