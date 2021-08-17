public class Class02 {
  Sub x;
  class Sub {
    static final int q = 3;
    { int j = 0; }
    Sub() { this(q); }
    int x = 0;

    Sub(int x) {}
  }
  Class02() { x = new Sub(); }
}