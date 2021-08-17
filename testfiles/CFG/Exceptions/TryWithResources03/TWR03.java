import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TWR03 {

  void m() {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      baos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      System.out.println("Finally!");
    }
  }
}