public class Expression03 {
  void m(int a, int c) { f(a > 0 && c<0, a> 0 || c < 0); }

  void f(boolean x, boolean y) {}
}