class MultiCatch04 {
  void n() {
    try {
      try {
        m();
        int a;
      } catch (TestException1a | TestException2a e) {
        System.out.println("First");
      }
    } catch (TestException1a | TestException2a e) {
      System.out.println("Second");
    } catch (TestException1 | TestException2 e) {
      System.out.println("Third");
    };
    int b;
  }

  void m() throws TestException1, TestException2 {}
}

class TestException1 extends RuntimeException {}
class TestException1a extends TestException1 {}
class TestException2 extends RuntimeException {}
class TestException2a extends TestException2 {}
