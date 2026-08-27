public class FAA07 {
    void testNestedForLoops() {
        int x = 0, y = 0; // @FAA
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                x = y; // @FAA
                y = x; // @FAA
            }
        }
    }
    
    void testNestedWhileLoops() {
        int a = 0, b = 0, c = 0; // @FAA @FAA
        while (true) {
            while (a < 10) {
                a = b; // @FAA
                b = c; // @FAA
                c = a; // @FAA
                a++;
            }
        }
    }
    
    void testMixedNestedLoops() {
        int p = 0, q = 0; // @FAA
        for (int i = 0; i < 10; i++) {
            while (p < 5) {
                p = q; // @FAA
                q = p; // @FAA
                p++;
            }
        }
    }
    
    void testTripleNestedCircular() {
        int m = 0, n = 0; // @FAA
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    m = n; // @FAA
                    n = m; // @FAA
                }
            }
        }
    }
    
    void testNestedWithBreakPattern() {
        int x = 0, y = 0, z = 0; // @FAA @FAA
        outer: for (int i = 0; i < 10; i++) {
            inner: for (int j = 0; j < 10; j++) {
                x = y; // @FAA
                y = z; // @FAA
                z = x; // @FAA
                
                if (i > 5) break outer;
                if (j > 5) break inner;
            }
        }
    }
}