public class Finally05 {
  static void fun() {
    while (true) {
      System.out.println("InWhile");
      try {
        int f;
        break;
      } finally {
        System.out.println("InFinally");
      }
    }
    System.out.println("AfterWhile");
  }
}