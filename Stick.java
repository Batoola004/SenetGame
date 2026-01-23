import java.util.Random;

public class Stick {
    private char faceColor;
    private Random random = new Random();

    public Stick() {

    }

    // تابع لاختيار لون العصا فاتح او غامق
    public char throwIt() {
        faceColor = random.nextBoolean() ? 'D' : 'L';
        return faceColor;
    }

    public char getFace() {
        return faceColor;
    }

    // تمثيل الشكل
    public String getVisual() {
        if (faceColor == 'D') {
            return "╿";
        } else {
            return "│";
        }
    }
}
