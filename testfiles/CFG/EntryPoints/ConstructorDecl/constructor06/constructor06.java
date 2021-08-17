public class constructor06 {
  constructor06(int x) { System.out.println("constructor06(int x)"); }
  constructor06() {
    this(2);
    System.out.println("constructor06()");
  }
}