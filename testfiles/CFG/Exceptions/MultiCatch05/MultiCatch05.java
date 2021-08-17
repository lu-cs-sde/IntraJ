class MultiCatch05 {
  void test() {
    try {
      try {
        m();
        int m;
      } catch (TestException1 e) {
        int n;
      }
    } catch (TestException1 | TestException3 e) {
      int a;
    } catch (TestException2 | TestException4 e) {
      int b;
    };
  }

  void m() throws TestException1, TestException2 {}
}

class TestException1 extends RuntimeException {}
class TestException2 extends RuntimeException {}
class TestException3 extends RuntimeException {}
class TestException4 extends RuntimeException {}