import java.util.*;

public class Rules {

    public static final int HOUSE_OF_REBIRTH = 15;
    public static final int HOUSE_OF_HAPPINESS = 26;
    public static final int HOUSE_OF_WATER = 27;
    public static final int HOUSE_OF_THREE_TRUTHS = 28;
    public static final int HOUSE_OF_RE_ATUM = 29;
    public static final int HOUSE_OF_HORUS = 30;

    public static List<Move> getPossibleMoves(int[] board, int player, int roll) {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < board.length; i++) {
            if (board[i] != player)
                continue;

            int from = i + 1;
            int to = from + roll;

            if (from < HOUSE_OF_HAPPINESS && to > HOUSE_OF_HAPPINESS) {
                continue;
            }

            if (from == HOUSE_OF_THREE_TRUTHS) {
                if (roll == 3) {
                    moves.add(new Move(from, -1));
                }
                continue;
            }

            if (from == HOUSE_OF_RE_ATUM) {
                if (roll == 2) {
                    moves.add(new Move(from, -1));
                }
                continue;
            }

            if (from == HOUSE_OF_HORUS) {
                moves.add(new Move(from, -1));
                continue;
            }

            if (to > BOARD_SIZE) {
                if (from > HOUSE_OF_HAPPINESS) {
                    moves.add(new Move(from, -1));
                }
                continue;
            }

            if (isValid(board, player, to)) {
                moves.add(new Move(from, to));
            }
        }

        return moves;
    }

    private static final int BOARD_SIZE = 30;

    private static boolean isValid(int[] board, int player, int sq) {
        int o = board[sq - 1];
        return o == 0 || (o != player && !isProtected(sq));
    }

    public static boolean isProtected(int sq) {
        // 15, 26, 28, 29, 30 are protected
        return sq == HOUSE_OF_REBIRTH ||
                sq == HOUSE_OF_HAPPINESS ||
                sq == HOUSE_OF_THREE_TRUTHS ||
                sq == HOUSE_OF_RE_ATUM ||
                sq == HOUSE_OF_HORUS;
    }

    public static int applySpecialSquareEffect(int sq, int[] board) {
        if (sq == HOUSE_OF_WATER) {
            return HOUSE_OF_REBIRTH; // Send to 15
        }
        return sq;
    }
}