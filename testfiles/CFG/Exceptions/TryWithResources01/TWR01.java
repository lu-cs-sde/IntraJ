
import java.io.ByteArrayOutputStream;
import java.io.IOException;
class TWR01 {
  void m() throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      baos.flush();
    }
  }
}