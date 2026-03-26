public class IMPDAA06 {
    void testCircularWithPrint() {
        int x, y;
        x = y;
        y = x;
        System.out.println(x + y);
    }
    
    void testCircularWithReturn() {
        int a, b; // @IMPDAA
        a = b; // @IMPDAA
        b = a;
    }
    
    void testPartialCircularWithUsage() {
        int p, q, r; // @IMPDAA
        p = q;
        q = r; // @IMPDAA
        r = q;
        System.out.println(p);
    }
    
    void testCircularWithAssignment() {
        int m, n, result; // @IMPDAA
        m = n; // @IMPDAA
        n = m;
        result = m;
    }
    
    void testCircularWithConditionalUsage() {
        int x, y; // @IMPDAA
        x = y; // @IMPDAA
        y = x;
        
        if (x > 0) {
            System.out.println("Positive");
        }
    }
}