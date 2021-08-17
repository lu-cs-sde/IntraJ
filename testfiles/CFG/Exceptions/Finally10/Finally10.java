public class Finally10 {
  void m() {
    try {
      int a = 0;
      try {
        if (a > 0)
          return;
      } finally {
        int b;
      }
      int c;
    } finally {
      int d;
    }
    int e;
  }
}