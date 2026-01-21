
import java.util.*;

public class Move {
    public final int from; // 0 = off-board, 1–30 = on board
    public final int to; // 1–30, or -1 if exited

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