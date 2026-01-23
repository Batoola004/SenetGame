
import java.util.Random;

public class Throw {
    private Stick[] sticks = new Stick[4];

    public Throw() {
        for (int i = 0; i < 4; i++)
            sticks[i] = new Stick();
    }

    public int makeThrow() {
        int total = 0;
        // لعد العصي الغامقة
        for (int i = 0; i < 4; i++) {
            if (sticks[i].throwIt() == 'D')
                total++;
        }
        // حساب المجموع وارجاعه
        if (total == 0)
            total = 5;
        return total;
    }
}