public class SwitchFallthrough02 {
    public void test(String input) {
        int x = 0;
        int y = 0;
        switch (input) {
            case "a":
                x = 1;
            case "b":
                y = 2;
            case "c":
                System.out.println(x + y);
                break;
        }
    }
}