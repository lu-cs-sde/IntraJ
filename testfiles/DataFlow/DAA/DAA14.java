class Color {
  Color(float a, float b, float c) {}
}
// Readapted from fop-0.95/src/java/org/apache/fop/util/ColorUtil.java:162
class DAA14 {

  private static Color parseAsJavaAWTColor(boolean b) throws RuntimeException {
    float red = 0.0f;
    float green = 0.0f;
    float blue = 0.0f;
    try {
      if (b) {
        red = Float.parseFloat("0");
        green = Float.parseFloat("0");
        blue = Float.parseFloat("0");
        if (red < 0.0 || green < 0.0 || blue > 1.0) {
          throw new RuntimeException("DAA");
        }
      } else {
        throw new IllegalArgumentException("DAA");
      }
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
    return new Color(red, green, blue);
  }
}