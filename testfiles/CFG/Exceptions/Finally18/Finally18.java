public class Finally18 {
  void m(int a, boolean b) {
    try {
      while (b) {
        try {
          if (b)
            break;
        } finally {
          continue;
        }
      }
      a++;
      if (b)
        return;
    } finally {
      a--;
    }
    a++;
  }
}
