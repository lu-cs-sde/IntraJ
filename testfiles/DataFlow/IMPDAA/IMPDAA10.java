public class IMPDAA10 {
    void testMixedDAA_IMPDAA() {
        int a = 0, b = 0, c = 0, d = 0; // @IMPDAA
        
        a = 1;
        a = 2;
        
        b = c; // @IMPDAA
        c = b;
        
        d = 5;
        System.out.println(d);
    }
    
    void testChainedMixed() {
        int x = 0, y = 0, z = 0, w = 0;
        
        x = 10; // @IMPDAA
        y = x; // @IMPDAA
        z = y; // @IMPDAA
        y = z;
        
        w = 20;
        System.out.println(w);
    }
    
    void testBrokenCircular() {
        int a = 0, b = 0, c = 0; // @IMPDAA
        
        a = b;
        b = c; // @IMPDAA
        c = b;
        
        System.out.println(a);
    }
    
    void testMultipleCirculars() {
        int p = 0, q = 0, r = 0, s = 0; // @IMPDAA @IMPDAA
        
        p = q; // @IMPDAA
        q = p;
        
        r = s; // @IMPDAA
        s = r;
    }
    
    void testPartialCircularWithDAA() {
        int m = 0, n = 0, o = 0; // @IMPDAA @IMPDAA
        
        m = 1;
        m = n; // @IMPDAA
        n = o;
        o = m;
    }
    
    void testComplexMixedPattern() {
        int var1 = 0, var2 = 0, var3 = 0, var4 = 0, var5 = 0; // @IMPDAA
        
        var1 = 100;
        
        var2 = var3; // @IMPDAA
        var3 = var2;
        
        var4 = var5;
        var5 = 200;
        var5 = var4;
        
        System.out.println(var4 + var5);
    }
}