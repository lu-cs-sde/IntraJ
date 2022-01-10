import java.util.function.BiFunction;
class Arithmetic {
  public static int add(int a, int b) { return a + b; }
}
public class MR03 {
  public static void main(String[] args) {
    BiFunction<Integer, Integer, Integer> adder = Arithmetic::add;
    int result = adder.apply(10, 20);
  }
}