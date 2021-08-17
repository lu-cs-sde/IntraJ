public class Finally07 {
  static void fun(int i) {
    while (i < 3) {
      try {
        System.out.println("Try");
        continue;
      } finally {
        i++;
        System.out.println("Finally");
      }
    }
    System.out.println("Out");
  }
}