public class FAA05 {
    void testBasicThreeWayCircle() {
        int x = 0, y = 0, z = 0; // @FAA @FAA
        x = y; // @FAA
        y = z;
        z = x;
    }
    
    void testThreeWayInLoop() {
        int a = 0, b = 0, c = 0; // @FAA @FAA
        for (int i = 0; i < 5; i++) {
            a = b; // @FAA
            b = c; // @FAA
            c = a; // @FAA
        }
    }
    
    void testComplexThreeWayChain() {
        int p = 0, q = 0, r = 0; // @FAA @FAA
        while (p < 100) {
            p = q + 1; // @FAA
            q = r * 2; // @FAA
            r = p / 3; // @FAA
        }
    }
    
    void testFourVariableChain() {
        int a = 0, b = 0, c = 0, d = 0; // @FAA @FAA @FAA
        a = b; // @FAA
        b = c;
        c = d;
        d = a;
    }
}