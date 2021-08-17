public class NA05 {
  void m() {
    Integer i = null;
    Integer j = null;
    foo(i);
    i.toString();
    j.toString();
  }
  void foo(Integer i){};
}