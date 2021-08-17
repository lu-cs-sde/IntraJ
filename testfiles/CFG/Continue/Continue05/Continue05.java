public class Continue05 {
  void m() {
  outer:
    for (int i = 0; i < 10; i++) {
    inner:
      switch (i) {
      case 1:
        int c;
        continue;
      case 2:
        int d;
        continue outer;
      case 3:
        int e;
        break;
      case 4:
        int f;
        break inner;
      case 5:
        int g;
        break outer;
      default:
        int k;
      }
      int y;
    }
    int z;
  }
}