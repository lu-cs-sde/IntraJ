public class IMPDAA03 {
    void f() {
        int i = 0, j = 0; // @IMPDAA
        while(true) {
            j = i; // @IMPDAA
            i = j; // @IMPDAA
        }
    }
}