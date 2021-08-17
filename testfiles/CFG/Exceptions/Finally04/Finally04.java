public class Finally04 {
  static void fun() {
    try {
      try {
        System.out.println("Try");
        { return; }
      } finally {
        System.out.println("Finally1");
      }
    } finally {
      System.out.println("Finally2");
    }
  }
}