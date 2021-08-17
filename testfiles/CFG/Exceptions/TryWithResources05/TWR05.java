import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TWR05 {
  void m() throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
         BufferedOutputStream bos = new BufferedOutputStream(baos)) {
      bos.flush();
    }
  }
}