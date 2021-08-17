public class Break07 {
  {
  outer:
    do {
      int d;
    inner:
      do {
        int c;
        break outer;
      } while (true);

    } while (true);
    int f;
  }
}