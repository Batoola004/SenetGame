
import java.util.*;

public class Move {
    public final int from;
    public final int to;

    public Move(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        String fromStr = (from == 0) ? "Off-board" : "Sq" + from;
        String toStr = (to == -1) ? "Exited" : "Sq" + to;
        return fromStr + " → " + toStr;
    }
}