public class FAA06 {
    void testCircularWithPrint() {
        int x = 0, y = 0;
        x = y;
        y = x;
        System.out.println(x + y);
    }
    
    void testCircularWithReturn() {
        int a = 0, b = 0; // @FAA
        a = b; // @FAA
        b = a;
    }
    
    void testPartialCircularWithUsage() {
        int p = 0, q = 0, r = 0; // @FAA
        p = q;
        q = r; // @FAA
        r = q;
        System.out.println(p);
    }
    
    void testCircularWithAssignment() {
        int m = 0, n = 0, result = 0; // @FAA
        m = n; // @FAA
        n = m;
        result = m;
    }
    
    void testCircularWithConditionalUsage() {
        int x = 0, y = 0; // @FAA
        x = y; // @FAA
        y = x;
        
        if (x > 0) {
            System.out.println("Positive");
        }
    }
}