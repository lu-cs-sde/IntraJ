public class IMPDAA04 {
    void testBasicCircular() {
        int x = 0, y = 0; // @IMPDAA
        x = y; // @IMPDAA
        y = x;
    }
    
    void testCircularInLoop() {
        int a = 0, b = 0; // @IMPDAA
        for (int i = 0; i < 10; i++) {
            a = b; // @IMPDAA
            b = a; // @IMPDAA
        }
    }
    
    void testCircularInWhile() {
        int m = 0, n = 0; // @IMPDAA
        while (true) {
            m = n; // @IMPDAA
            n = m; // @IMPDAA
        }
    }
}