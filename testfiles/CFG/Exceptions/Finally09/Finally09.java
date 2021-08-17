public class Finally09 {
  void m2() {
    try {
      int a = 0;
      if (a > 0)
        return;
    } finally {
      int b;
    }
    int c;
  }
}