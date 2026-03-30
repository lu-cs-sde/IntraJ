public class DAA12 {
  static void foo(int t) { t = 120; } // @DAA
  public static void main(String[] args) {
    int i = 10;
    System.out.println("i: " + i);
    foo(i);
    System.out.println("i: " + i);
  }
}