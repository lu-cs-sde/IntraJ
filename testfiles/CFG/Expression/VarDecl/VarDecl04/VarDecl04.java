public class VarDecl04 {
  void m() {
    int a;
    int b;
    int x = add(a = 8, b = 9);
  }

  int add(int a, int b) { return a + b; }
}