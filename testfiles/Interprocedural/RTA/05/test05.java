public class test05 {
  void foo(){};
}

class test extends test05 {
  @Override
  void foo() {
    // test::foo
  }
  void q() { foo(); }
}
