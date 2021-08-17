// Readapted from fop-0.95/src/java/org/apache/fop/fonts/FontUtil.java:35
public class DAA13 {
  int foo(boolean b, String text) {
    int weight = 400;
    try {
      weight = Integer.parseInt(text);
      weight = ((int)weight / 100) * 100;
      weight = Math.max(weight, 100);
      weight = Math.min(weight, 900);
    } catch (NumberFormatException nfe) {
      if (text.equals("normal")) {
        weight = 400;
      } else if (text.equals("bold")) {
        weight = 700;
      } else {
        throw new IllegalArgumentException("DAA13");
      }
    }
    return weight;
  }
}