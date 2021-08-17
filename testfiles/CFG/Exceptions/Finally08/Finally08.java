public class Finally08 {
  static void fun(int i) {
  outer:
    do {
      try {
      inner:
        while (i < 3) {
          try {
            System.out.println("Try");
            continue outer;
          } finally {
            i++;
            System.out.println("Finally");
          }
        }
        System.out.println("out1");
      } finally {
        System.out.println("Finally2");
      }
    } while (i < 3);
    System.out.println("out2");
  }
}
/**
 * If i>=3 this program prints:
      out1
      Finally2
      out2
 */