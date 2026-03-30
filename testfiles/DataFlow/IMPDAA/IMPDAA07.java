public class IMPDAA07 {
    void testNestedForLoops() {
        int x = 0, y = 0; // @IMPDAA
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                x = y; // @IMPDAA
                y = x; // @IMPDAA
            }
        }
    }
    
    void testNestedWhileLoops() {
        int a = 0, b = 0, c = 0; // @IMPDAA @IMPDAA
        while (true) {
            while (a < 10) {
                a = b; // @IMPDAA
                b = c; // @IMPDAA
                c = a; // @IMPDAA
                a++;
            }
        }
    }
    
    void testMixedNestedLoops() {
        int p = 0, q = 0; // @IMPDAA
        for (int i = 0; i < 10; i++) {
            while (p < 5) {
                p = q; // @IMPDAA
                q = p; // @IMPDAA
                p++;
            }
        }
    }
    
    void testTripleNestedCircular() {
        int m = 0, n = 0; // @IMPDAA
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    m = n; // @IMPDAA
                    n = m; // @IMPDAA
                }
            }
        }
    }
    
    void testNestedWithBreakPattern() {
        int x = 0, y = 0, z = 0; // @IMPDAA @IMPDAA
        outer: for (int i = 0; i < 10; i++) {
            inner: for (int j = 0; j < 10; j++) {
                x = y; // @IMPDAA
                y = z; // @IMPDAA
                z = x; // @IMPDAA
                
                if (i > 5) break outer;
                if (j > 5) break inner;
            }
        }
    }
}