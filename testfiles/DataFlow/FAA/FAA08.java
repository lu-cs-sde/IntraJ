public class FAA08 {
    void testIfElseCircular(boolean condition) {
        int x = 0, y = 0; // @FAA
        
        if (condition) {
            x = y; // @FAA
            y = x;
        } else {
            x = 5;
            y = 10;
        }
    }
    
    void testSwitchCircular(int value) {
        int a = 0, b = 0, c = 0; // @FAA @FAA
        
        switch (value) {
            case 1:
                a = b; // @FAA
                b = a;
                break;
            case 2:
                b = c; // @FAA
                c = b;
                break;
            default:
                a = 1;
                break;
        }
    }
    
    void testConditionalInLoop() {
        int m = 0, n = 0; // @FAA
        
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                m = n; // @FAA
                n = m; // @FAA
            }
        }
    }
    
    void testNestedConditionalCircular(boolean outer, boolean inner) {
        int p = 0, q = 0, r = 0; // @FAA @FAA
        
        if (outer) {
            if (inner) {
                p = q; // @FAA
                q = r;
                r = p;
            } else {
                p = 1;
            }
        }
    }
    
    void testTernaryCircular(boolean condition) {
        int x = 0, y = 0, temp = 0; // @FAA @FAA
        
        temp = condition ? (x = y) : (y = x);
    }
    
    void testTryCatchCircular() {
        int a = 0, b = 0; // @FAA
        
        try {
            a = b; // @FAA
            b = a;
            throw new RuntimeException("test");
        } catch (Exception e) {
        }
    }
}