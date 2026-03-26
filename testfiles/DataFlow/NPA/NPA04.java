public class NA04 {
  void m() {
    Integer tmp = null;
    if (tmp == null) {
      tmp.toString(); // @NPA
      tmp = new Integer(2);
      tmp.toString();
    }
    tmp.toString();
  }
}