public class DAA11 {
  static void foo(StringBuffer t) { t = t.append("Test"); }
  public static void main(String[] args) {
    StringBuffer s = new StringBuffer("Test");
    int p = s.length();
    int q = s.capacity();
    System.out.println("length: " + p);
    System.out.println("capacity: " + q);
    foo(s);
    p = s.length();
    q = s.capacity();
    System.out.println("length: " + p);
    System.out.println("capacity: " + q);
  }
}