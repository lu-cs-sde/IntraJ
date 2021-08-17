class Polygon {
  Polygon(String x) {}
  public void display() { System.out.println("Inside the Polygon class"); }
}

class Anonymous01 {
  public void createClass() {
    Polygon p1 = new Polygon("test") {
      public void display() {
        System.out.println("Inside an anonymous class.");
      }
    };
    p1.display();
  }
}
