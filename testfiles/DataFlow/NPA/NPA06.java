public class NA06 {
  void m(Boolean b) {
    Integer i = new Integer(2);
    i = foo(i);
    i.toString();
    i = foo3();
    i.toString();
    i = foo2();
    i.toString();
    return;
  }
  Integer foo3() { return 3; }
  Integer foo2() {
    Integer i = null;
    return i;
  }
  Integer foo(Integer i) { return null; }
}
