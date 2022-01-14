interface StringFunction {
  String run(String str);
}

public class Lambda04 {
  void foo() {
    StringFunction exclaim = (s) -> s + "!";
    exclaim.run("Test");
  }
}