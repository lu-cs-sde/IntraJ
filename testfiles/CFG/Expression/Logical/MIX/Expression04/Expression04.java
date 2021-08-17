public class Expression04 {
  void m(int a, int c) {
    boolean g;
    f(g = a > 0 && c<0, g = a> 0 || c < 0);
  }

  void f(boolean x, boolean y) {}
}