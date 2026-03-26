public class IMPDAA03 {
    void f() {
        int i, j; // @IMPDAA
        while(true) {
            j = i; // @IMPDAA
            i = j; // @IMPDAA
        }
    }
}