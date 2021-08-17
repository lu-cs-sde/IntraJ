class Polygon {
  Polygon(String x) {}
  public void display() { System.out.println("Inside the Polygon class"); }
}

class Anonymous02 {
  public void createClass() {
    Polygon p1 = new Polygon("test") {
      { int x = 0; }
      public void display() {
        System.out.println("Inside an anonymous class.");
      }
      { int y = 0; }
    };
    p1.display();
  }
}
