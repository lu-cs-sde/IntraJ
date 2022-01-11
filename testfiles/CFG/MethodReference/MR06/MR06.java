interface Messageable {
  Message getMessage(String msg);
}
class Message {
  Message(String msg) { System.out.print(msg); }
}
public class MR06 {
  public static void main(String[] args) {
    Messageable foo = Message::new;
    foo.getMessage("MR06");
  }
}