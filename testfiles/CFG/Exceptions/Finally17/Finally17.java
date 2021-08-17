public class Finally17 {

  void m() {
    int a = 0;
    try {
      try {
        int b = 0;
        if (b > 0)
          return;
      } finally {
        int c = 0;
        if (c > 0)
          throw new TestException();
      }
      int d;
    } catch (TestException e) {
      int f;
    }
    int g;
  }
}

class TestException extends Exception {}