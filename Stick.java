import java.util.Random;

public class Stick {
    private char faceColor;
    private Random random = new Random();

    public Stick() {

    }

    public char throwIt() {
        faceColor = random.nextBoolean() ? 'D' : 'L';
        return faceColor;
    }

    public char getFace() {
        return faceColor;
    }

    public String getVisual() {
        if (faceColor == 'D') {
            return "╿";
        } else {
            return "│";
        }
    }
}
