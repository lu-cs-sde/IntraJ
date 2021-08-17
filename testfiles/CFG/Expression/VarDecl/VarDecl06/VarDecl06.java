public class VarDecl06 {
  void m() {
    int a;
    int[][] array2 = new int[a = 5][];
    array2[0][10] = 6;
  }
}