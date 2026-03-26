public class IMPDAA05 {
    void testBasicThreeWayCircle() {
        int x, y, z; // @IMPDAA @IMPDAA
        x = y; // @IMPDAA
        y = z;
        z = x;
    }
    
    void testThreeWayInLoop() {
        int a, b, c; // @IMPDAA @IMPDAA
        for (int i = 0; i < 5; i++) {
            a = b; // @IMPDAA
            b = c; // @IMPDAA
            c = a; // @IMPDAA
        }
    }
    
    void testComplexThreeWayChain() {
        int p, q, r; // @IMPDAA @IMPDAA
        while (p < 100) {
            p = q + 1; // @IMPDAA
            q = r * 2; // @IMPDAA
            r = p / 3; // @IMPDAA
        }
    }
    
    void testFourVariableChain() {
        int a, b, c, d; // @IMPDAA @IMPDAA @IMPDAA
        a = b; // @IMPDAA
        b = c;
        c = d;
        d = a;
    }
}