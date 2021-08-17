public class Finally14 {
  static void m() {
    try {
      int a;
      throw new TestException();
    } catch (TestException e) {
      System.out.println("Catch");
      int b;
    } finally {
      System.out.println("Finally");
      int c;
    }
    System.out.println("Out");
    int d;
  }
}
class TestException extends Exception {}