public class FAA04 {
    void testBasicCircular() {
        int x = 0, y = 0; // @FAA
        x = y; // @FAA
        y = x;
    }
    
    void testCircularInLoop() {
        int a = 0, b = 0; // @FAA
        for (int i = 0; i < 10; i++) {
            a = b; // @FAA
            b = a; // @FAA
        }
    }
    
    void testCircularInWhile() {
        int m = 0, n = 0; // @FAA
        while (true) {
            m = n; // @FAA
            n = m; // @FAA
        }
    }
}