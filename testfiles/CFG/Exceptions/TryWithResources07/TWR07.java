import java.io.*;

public class TWR07 {
  void m() throws IOException {
    try (Closeable os = new ByteArrayOutputStream()) {
      ((Flushable)os).flush();
    }
  }
}