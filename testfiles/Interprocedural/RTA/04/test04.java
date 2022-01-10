// test::q calls test04::m
public class test04 {
  void m(){};
}

class test extends test04 {
  void q() { m(); }
}
