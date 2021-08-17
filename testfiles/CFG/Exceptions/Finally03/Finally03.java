public class Finally03 {
  static int fun() {
    try {
      try {
        System.out.println("Try");
        { return 4; }
      } finally {
        System.out.println("Finally1");
      }
    } finally {
      System.out.println("Finally2");
    }
  }
}