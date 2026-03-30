public class NPA21 {
  void m(String args) {
    if (args == null) {
      System.out.println("Test");
    }
    args.toString(); // @NPA
  }
}