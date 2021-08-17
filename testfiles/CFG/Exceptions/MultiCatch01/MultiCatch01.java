class MultiCatch01 {
  void m() {
    try {
      if (true) {
        throw new TestException1();
      } else {
        throw new TestException2();
      }
    } catch (TestException1 | TestException2 e) {
      int a;
    }
    int b;
  }
}

class TestException1 extends Exception {}
class TestException2 extends Exception {}