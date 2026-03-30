import java.io.IOException;

public class DAA02 {
  void m(boolean b) {
    int i = 0;
    try {
      try {
        if (b)
          foo();
      } finally {
        i = 10; // @DAA
      }
    } catch (IOException e) {
      i = 10; // @DAA
    } finally {
      i = 30; // @DAA
    }
    return;
  }

  void foo() throws IOException {}
}