public class IMPDAA09 {
    void testBasicSelfAssignment() {
        int x;
        x = 5;
        x = x;
        System.out.println(x);
    }
    
    void testSelfAssignmentInExpression() {
        int y;
        y = 10;
        y = y + 0;
        System.out.println(y);
    }
    
    void testSelfAssignmentChain() {
        int a, b;
        a = 1;
        b = a;
        a = a;
        System.out.println(b);
    }
    
    void testComplexSelfAssignment() {
        int m, n; // @IMPDAA
        m = n;
        n = n; // @IMPDAA
        m = n;
    }
    
    void testSelfAssignmentInLoop() {
        int counter = 0;
        for (int i = 0; i < 10; i++) {
            counter = counter;
            counter++;
        }
        System.out.println(counter);
    }
    
    void testArraySelfAssignment() {
        int[] arr = new int[5];
        arr[0] = 10;
        arr[0] = arr[0];
        System.out.println(arr[0]);
    }
    
    class InnerTestClass {
        int field;
        
        void testInstanceSelfAssignment() {
            field = 5;
            field = field;
            System.out.println(field);
        }
    }
}