public class IMPDAA10 {
    void testMixedDAA_IMPDAA() {
        int a, b, c, d; // @IMPDAA
        
        a = 1;
        a = 2;
        
        b = c; // @IMPDAA
        c = b;
        
        d = 5;
        System.out.println(d);
    }
    
    void testChainedMixed() {
        int x, y, z, w;
        
        x = 10; // @IMPDAA
        y = x; // @IMPDAA
        z = y; // @IMPDAA
        y = z;
        
        w = 20;
        System.out.println(w);
    }
    
    void testBrokenCircular() {
        int a, b, c; // @IMPDAA
        
        a = b;
        b = c; // @IMPDAA
        c = b;
        
        System.out.println(a);
    }
    
    void testMultipleCirculars() {
        int p, q, r, s; // @IMPDAA @IMPDAA
        
        p = q; // @IMPDAA
        q = p;
        
        r = s; // @IMPDAA
        s = r;
    }
    
    void testPartialCircularWithDAA() {
        int m, n, o; // @IMPDAA @IMPDAA
        
        m = 1;
        m = n; // @IMPDAA
        n = o;
        o = m;
    }
    
    void testComplexMixedPattern() {
        int var1, var2, var3, var4, var5; // @IMPDAA
        
        var1 = 100;
        
        var2 = var3; // @IMPDAA
        var3 = var2;
        
        var4 = var5;
        var5 = 200;
        var5 = var4;
        
        System.out.println(var4 + var5);
    }
}