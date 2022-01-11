
public class Lambda02 {
  void foo() {
    Integer x = 0;
    Runnable r = () -> { System.out.println(x); };
    r.run();
  }
}