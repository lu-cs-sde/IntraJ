
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class TWR02 {
  void m() {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      baos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}