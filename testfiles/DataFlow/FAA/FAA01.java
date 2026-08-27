public class FAA01 {
  void foo() {
    Integer x = 2;
    Integer y = 3; // @FAA
    x = y; // @FAA
    y = x;
  }
}