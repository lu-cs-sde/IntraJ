// Using predefined functional interface Runnable to refer static method.
public class MR02 {
  public static void bar() {}
  public static void main(String[] args) {
    Thread t2 = new Thread(MR02::bar);
    t2.start();
  }
}