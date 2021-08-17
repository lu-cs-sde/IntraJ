class MultiCatch02 {
  void n() throws Exception {
    try {
      m();
      int f;
    } catch (TestException1 | TestException2 e) {
      int q;
    }
    int g;
  }

  void m() throws TestException1, TestException2, TestException3 {}
}

class TestException1 extends Exception {}
class TestException2 extends Exception {}
class TestException3 extends Exception {}