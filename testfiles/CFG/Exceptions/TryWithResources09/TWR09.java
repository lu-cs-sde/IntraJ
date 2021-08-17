import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TWR09 {

  void m() throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      try (BufferedOutputStream bos = new BufferedOutputStream(baos)) {
        bos.flush();
      }
    } catch (IOException t) {
      int c;
    } finally {
      int d;
    }
  }
}