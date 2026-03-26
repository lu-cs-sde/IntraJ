public class MR05 {
  public void bar() {}
  public static void main(String[] args) {
    Thread t2 = new Thread(new MR05()::bar);
    t2.start();
  }
}