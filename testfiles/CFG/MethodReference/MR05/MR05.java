public class MR2 {
  public void bar() {}
  public static void main(String[] args) {
    Thread t2 = new Thread(new MR2()::bar);
    t2.start();
  }
}