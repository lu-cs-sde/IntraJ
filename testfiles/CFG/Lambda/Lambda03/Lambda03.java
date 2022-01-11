import java.util.ArrayList;

public class Lambda03 {
  public static void main(String[] args) {
    ArrayList<Integer> numbers = new ArrayList<Integer>();
    numbers.forEach((n) -> { System.out.println(n); });
  }
}
