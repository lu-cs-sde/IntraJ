public class IMPDAA06 {
    void testCircularWithPrint() {
        int x = 0, y = 0;
        x = y;
        y = x;
        System.out.println(x + y);
    }
    
    void testCircularWithReturn() {
        int a = 0, b = 0; // @IMPDAA
        a = b; // @IMPDAA
        b = a;
    }
    
    void testPartialCircularWithUsage() {
        int p = 0, q = 0, r = 0; // @IMPDAA
        p = q;
        q = r; // @IMPDAA
        r = q;
        System.out.println(p);
    }
    
    void testCircularWithAssignment() {
        int m = 0, n = 0, result = 0; // @IMPDAA
        m = n; // @IMPDAA
        n = m;
        result = m;
    }
    
    void testCircularWithConditionalUsage() {
        int x = 0, y = 0; // @IMPDAA
        x = y; // @IMPDAA
        y = x;
        
        if (x > 0) {
            System.out.println("Positive");
        }
    }
}