interface FI {
  void bar();
}
class MethodReference {
  public static void foo() {}
  public static void main(String[] args) {
    FI fr = MethodReference::foo; // Static reference to method foo
    fr.bar();
  }
}