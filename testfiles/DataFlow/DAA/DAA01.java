public class DAA01 {
  void m() {
    int a = 0;
    a++; // @DAA
    a = 0 + 1;
    a++; // @DAA
    a = 0;
    a++;
    a++; // @DAA
    a = 0 + 1; // @DAA
  }
}
