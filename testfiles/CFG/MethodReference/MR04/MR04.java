interface Sayable {
  void say();
}
public class MR04 {
  public void foo() {}
  public static void main(String[] args) {
    // Creating object  referring non-static method using reference
    MR04 methodReference = new MR04();
    Sayable sayable = methodReference::foo;

    sayable.say();
    // Referring non-static method using anonymous object
    Sayable sayable2 = new MR04()::foo;
    sayable2.say();
  }
}