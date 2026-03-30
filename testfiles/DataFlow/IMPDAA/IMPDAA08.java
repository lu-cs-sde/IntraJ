public class IMPDAA08 {
    void testIfElseCircular(boolean condition) {
        int x = 0, y = 0; // @IMPDAA
        
        if (condition) {
            x = y; // @IMPDAA
            y = x;
        } else {
            x = 5;
            y = 10;
        }
    }
    
    void testSwitchCircular(int value) {
        int a = 0, b = 0, c = 0; // @IMPDAA @IMPDAA
        
        switch (value) {
            case 1:
                a = b; // @IMPDAA
                b = a;
                break;
            case 2:
                b = c; // @IMPDAA
                c = b;
                break;
            default:
                a = 1;
                break;
        }
    }
    
    void testConditionalInLoop() {
        int m = 0, n = 0; // @IMPDAA
        
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                m = n; // @IMPDAA
                n = m; // @IMPDAA
            }
        }
    }
    
    void testNestedConditionalCircular(boolean outer, boolean inner) {
        int p = 0, q = 0, r = 0; // @IMPDAA @IMPDAA
        
        if (outer) {
            if (inner) {
                p = q; // @IMPDAA
                q = r;
                r = p;
            } else {
                p = 1;
            }
        }
    }
    
    void testTernaryCircular(boolean condition) {
        int x = 0, y = 0, temp = 0; // @IMPDAA @IMPDAA
        
        temp = condition ? (x = y) : (y = x);
    }
    
    void testTryCatchCircular() {
        int a = 0, b = 0; // @IMPDAA
        
        try {
            a = b; // @IMPDAA
            b = a;
            throw new RuntimeException("test");
        } catch (Exception e) {
        }
    }
}