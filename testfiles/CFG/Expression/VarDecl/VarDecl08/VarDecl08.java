public class VarDecl08 {
  void m() {
    class VD {
      private int a = 1 + 1;
      public VD(int a, int b) {
        this.a = a;
        this.a = b;
      }
    }

    int a, b;
    VD[][] vd3 = new VD[2][b = 3];
    vd3[a = 0][b = 0] = new VD(a = 2, b = 5);
  }
}