class Person {
  private String name;
  Person(String name) { this.name = name; }
  String getName() { return name; }
}

public class NA09 {
  public static void main(String args[]) {
    Person x = null;
    Boolean b = false;
    if (b) {
      x = new Person("M");
    } else {
      // Nothing
    }
    System.out.println(x.getName());
  }
}