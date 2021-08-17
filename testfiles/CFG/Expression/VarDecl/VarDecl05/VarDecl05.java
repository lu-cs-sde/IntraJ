public class VarDecl05 {
  void m() {
    int a;
    int x = add(a = 8, a);
  }

  int add(int a, int b) { return a + b; }
}