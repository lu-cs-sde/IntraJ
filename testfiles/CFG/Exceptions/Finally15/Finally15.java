public class Finally15 {
  static void m() throws TestException {
    try {
      int a = 1;
      if (a > 0)
        throw new TestSubException();
    } catch (TestSubException e) {
      System.out.println("Catch");
      throw new TestException();
    } finally {
      System.out.println("Finally");
      int c;
    }
    int d;
  }
}

class TestException extends Exception {}

class TestSubException extends TestException {}
