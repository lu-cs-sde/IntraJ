class MultiCatch03 {
  void n() {
    try {
      try {
        m();
        int q;
      } catch (TestException3 e) {
        int g;
      }
    } catch (TestException1 | TestException2 e) {
      int f;
    };
  }

  void m() throws TestException1, TestException2, TestException3 {}
}

class TestException1 extends Exception {}
class TestException2 extends Exception {}
class TestException3 extends Exception {}