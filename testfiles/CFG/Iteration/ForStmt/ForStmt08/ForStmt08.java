public class ForStmt08 {
  {
    int a;
    int b;
    int i = 0;
    int j = 0;
    boolean f = true;
    boolean g = false;
    for (; i < 10; f = f && g, g = g || f) {
      a = 8;
    }
    int c;
  }
}

/**
 * Testing short circuit evaluation in UpdateStmt
 */