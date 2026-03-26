public class IMPDAA04 {
    void testBasicCircular() {
        int x, y; // @IMPDAA
        x = y; // @IMPDAA
        y = x;
    }
    
    void testCircularInLoop() {
        int a, b; // @IMPDAA
        for (int i = 0; i < 10; i++) {
            a = b; // @IMPDAA
            b = a; // @IMPDAA
        }
    }
    
    void testCircularInWhile() {
        int m, n; // @IMPDAA
        while (true) {
            m = n; // @IMPDAA
            n = m; // @IMPDAA
        }
    }
}