public class Finally16 {

  void m12() throws TestException {
    int a = 0;
    while (a > 0) {
      try {
        if (a > 0)
          break;
      } finally {
        int c;
        throw new TestException();
      }
    }
    int b;
  }
}
class TestException extends Exception {}