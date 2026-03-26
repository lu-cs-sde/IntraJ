public class NA05 {
  void m() {
    Integer i = null;
    Integer j = null;
    foo(i);
    i.toString(); // @NPA
    j.toString(); // @NPA
  }
  void foo(Integer i){};
}