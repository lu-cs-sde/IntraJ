public class FAA03 {
    void f() {
        int i = 0, j = 0; // @FAA
        while(true) {
            j = i; // @FAA
            i = j; // @FAA
        }
    }
}