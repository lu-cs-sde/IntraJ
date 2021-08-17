import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TWR04 {

  void m() throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      baos.flush();
    } finally {
      System.out.println("Finally!");
    }
  }
}
