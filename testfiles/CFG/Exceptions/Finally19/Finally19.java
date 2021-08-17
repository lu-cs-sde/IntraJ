public class Finally19 {

  void m(boolean a) {
    try {
      try {
        if (a) {
          m2();
        }
        int g;
      } catch (TestSubException e) {
        int b;
      } finally {
        int c;
      }
    } catch (TestException e) {
      int d;
    } finally {
      int e;
    }
    int f;
  }

  void m2() throws TestException {}

  class TestException extends Exception {}
  class TestSubException extends TestException {}
}
