public class Break08 {
  void m() {
  outer:
    do {
      int d;
    inner:
      do {
        int c;
        break inner;
      } while (true);
      int q;
    } while (true);
  }
}