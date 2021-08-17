public class Finally13 {
  void m10() throws TestException {
    try {
      int a;
      throw new TestException();
    } catch (TestSubException e) {
      int b;
    } finally {
      int c;
    }
    int d;
  }
}

class TestException extends Exception {}

class TestSubException extends TestException {}