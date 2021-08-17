class MultiCatch06 {
  void test() {
    try {
      try {
        m();
        int m;
      } catch (TestException1 | TestException2 e) {
        int a;
      }
    } catch (TestException2 e) {
      int b;
    };
    int c;
  }

  void m() throws TestException1, TestException2 {}
}

class TestException1 extends RuntimeException {}
class TestException2 extends RuntimeException {}
