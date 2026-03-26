public class SwitchFallthrough03 {
    public void test(int value) {
        int a = 0;
        int b = 0;
        switch (value) {
            case 1:
                a = 10;
            case 2:
            case 3:
                b = 20;
                System.out.println(a + b);
                break;
            default:
                System.out.println("default");
        }
    }
}