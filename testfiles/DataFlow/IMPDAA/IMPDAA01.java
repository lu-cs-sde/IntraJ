public class IMPDAA01 {
  void foo() {
    Integer x = 2;
    Integer y = 3; // @IMPDAA
    x = y; // @IMPDAA
    y = x;
  }
}