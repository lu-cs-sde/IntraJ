public class IMPDAA02 {
  void foo(Boolean b) {
    String y = "Test";
    String x = y; // @DAA
    if (b) {
      x = "test";
    } else {
      x = "";
    }
    System.out.println(x);
  }
}