public class Switch01 {
  void m() {
    int a = 0;
    switch (a = 1) {
    case 1: {
      a = 2;
    }
    case 1 + 1:
      a = 3;
      a = 4;
    default:
      a = 5;
    }
  }
}