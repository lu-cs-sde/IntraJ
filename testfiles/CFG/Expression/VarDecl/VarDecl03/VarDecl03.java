public class VarDecl03 {
  void m() {
    int a, b;
    int[][] array1 = new int[a = 2][b = 3];
    array1[a = 1][b = 2] = 5;
  }
}