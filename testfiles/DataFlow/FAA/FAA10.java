public class FAA10 {
    void testMixedDAA_FAA() {
        int a = 0, b = 0, c = 0, d = 0; // @FAA
        
        a = 1;
        a = 2;
        
        b = c; // @FAA
        c = b;
        
        d = 5;
        System.out.println(d);
    }
    
    void testChainedMixed() {
        int x = 0, y = 0, z = 0, w = 0;
        
        x = 10; // @FAA
        y = x; // @FAA
        z = y; // @FAA
        y = z;
        
        w = 20;
        System.out.println(w);
    }
    
    void testBrokenCircular() {
        int a = 0, b = 0, c = 0; // @FAA
        
        a = b;
        b = c; // @FAA
        c = b;
        
        System.out.println(a);
    }
    
    void testMultipleCirculars() {
        int p = 0, q = 0, r = 0, s = 0; // @FAA @FAA
        
        p = q; // @FAA
        q = p;
        
        r = s; // @FAA
        s = r;
    }
    
    void testPartialCircularWithDAA() {
        int m = 0, n = 0, o = 0; // @FAA @FAA
        
        m = 1;
        m = n; // @FAA
        n = o;
        o = m;
    }
    
    void testComplexMixedPattern() {
        int var1 = 0, var2 = 0, var3 = 0, var4 = 0, var5 = 0; // @FAA
        
        var1 = 100;
        
        var2 = var3; // @FAA
        var3 = var2;
        
        var4 = var5;
        var5 = 200;
        var5 = var4;
        
        System.out.println(var4 + var5);
    }
}