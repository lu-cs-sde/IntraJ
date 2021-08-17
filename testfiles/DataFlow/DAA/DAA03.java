import java.io.IOException;

public class DAA03 {
  int m(boolean b) {
    int i = 0;
    try {
      try {
        if (b)
          foo();
      } finally {
        i = 10;
      }
    } catch (IOException e) {
      i = 10;
    } finally {
      i = 30;
    }
    return i;
  }

  void foo() throws IOException {}
}