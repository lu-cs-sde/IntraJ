import java.io.IOException;

public class TWR12 {
  public static void main(String[] args)
      throws TestCloseable2.ConstructorException, TestCloseable1.CloseException,
             TestCloseable1.ConstructorException {
    try (TestCloseable1 r1 = new TestCloseable1();
         TestCloseable2 r2 = new TestCloseable2()) {
      System.out.println("Body");
      throw new RuntimeException();
    } catch (RuntimeException t) {
      System.out.println("Catch");
    } finally {
      System.out.println("Finally");
    }
  }
}

class TestCloseable1 implements AutoCloseable {
  public TestCloseable1() throws ConstructorException {}

  @Override
  public void close() throws CloseException {}

  class CloseException extends Exception {}
  class ConstructorException extends Exception {}
}

class TestCloseable2 implements AutoCloseable {
  public TestCloseable2() throws ConstructorException {}
  @Override
  public void close() {}

  class ConstructorException extends Exception {}
}