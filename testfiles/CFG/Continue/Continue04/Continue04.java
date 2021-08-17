public class Continue04 {
  void m() {
    boolean b = false;
  outer:
    while (b) {
      int q;
    inner:
      for (int f = 0;; f++) {
        int c;
        if (b)
          continue outer;
        else
          continue inner;
      }
    }
  }
}