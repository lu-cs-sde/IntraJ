public class Finally02 {
  static int fun() {
    try {
      try {
        System.out.println("Try");
        { return 4; }
      } finally {
        System.out.println("Finally1");
        return 3;
      }
    } finally {
      System.out.println("Finally2");
    }
  }
}