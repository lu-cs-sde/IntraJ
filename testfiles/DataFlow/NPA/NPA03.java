public class NPA03 {
  void m() {
    Object a = null;
    Object o = a;
    a.toString(); // @NPA
    o.toString(); // @NPA
  }
}