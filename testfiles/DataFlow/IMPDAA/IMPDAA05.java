public class IMPDAA05 {
    void testBasicThreeWayCircle() {
        int x = 0, y = 0, z = 0; // @IMPDAA @IMPDAA
        x = y; // @IMPDAA
        y = z;
        z = x;
    }
    
    void testThreeWayInLoop() {
        int a = 0, b = 0, c = 0; // @IMPDAA @IMPDAA
        for (int i = 0; i < 5; i++) {
            a = b; // @IMPDAA
            b = c; // @IMPDAA
            c = a; // @IMPDAA
        }
    }
    
    void testComplexThreeWayChain() {
        int p = 0, q = 0, r = 0; // @IMPDAA @IMPDAA
        while (p < 100) {
            p = q + 1; // @IMPDAA
            q = r * 2; // @IMPDAA
            r = p / 3; // @IMPDAA
        }
    }
    
    void testFourVariableChain() {
        int a = 0, b = 0, c = 0, d = 0; // @IMPDAA @IMPDAA @IMPDAA
        a = b; // @IMPDAA
        b = c;
        c = d;
        d = a;
    }
}