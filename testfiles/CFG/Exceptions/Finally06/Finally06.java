public class Finally06 {
  static void fun(boolean b) {
    while (b) {
      System.out.println("InWhile");
      try {
        int f;
        break;
      } finally {
        System.out.println("InFinally");
        return;
      }
    }
    System.out.println("AfterWhile");
  }
}