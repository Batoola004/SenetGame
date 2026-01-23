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

            // --- 1. HOUSE OF HAPPINESS RULE (Square 26) ---
            // You cannot jump over 26. You must land exactly on it.
            if (from < HOUSE_OF_HAPPINESS && to > HOUSE_OF_HAPPINESS) {
                continue;
            }

            // --- 2. SPECIAL HOUSE EXIT RULES (Squares 28, 29, 30) ---

            // House of Three Truths (28): Only exits on roll of 3
            if (from == HOUSE_OF_THREE_TRUTHS) {
                if (roll == 3) {
                    moves.add(new Move(from, -1));
                }
                continue; // Cannot move to 29 or 30, only exit
            }

            // House of Re-Atum (29): Only exits on roll of 2
            if (from == HOUSE_OF_RE_ATUM) {
                if (roll == 2) {
                    moves.add(new Move(from, -1));
                }
                continue;
            }

            // House of Horus (30): Exits on ANY roll
            if (from == HOUSE_OF_HORUS) {
                moves.add(new Move(from, -1));
                continue;
            }

            // --- 3. STANDARD MOVE VALIDATION ---

            // Standard exit rule: Can exit if moving past 30
            if (to > BOARD_SIZE) {
                // To exit, piece must generally be past House of Happiness
                if (from > HOUSE_OF_HAPPINESS) {
                    moves.add(new Move(from, -1));
                }
                continue;
            }

            // Check if destination is valid (Empty or unprotected opponent)
            if (isValid(board, player, to)) {
                moves.add(new Move(from, to));
            }
        }

        return moves;
    }

    private static final int BOARD_SIZE = 30;

    private static boolean isValid(int[] board, int player, int sq) {
        int o = board[sq - 1];
        // Valid if empty OR opponent is not protected
        return o == 0 || (o != player && !isProtected(sq));
    }

    public static boolean isProtected(int sq) {
        // Squares 15, 26, 28, 29, 30 are safe zones/protected
        return sq == HOUSE_OF_REBIRTH ||
                sq == HOUSE_OF_HAPPINESS ||
                sq == HOUSE_OF_THREE_TRUTHS ||
                sq == HOUSE_OF_RE_ATUM ||
                sq == HOUSE_OF_HORUS;
    }

    /**
     * Immediate effects upon landing (House of Water).
     */
    public static int applySpecialSquareEffect(int sq, int[] board) {
        if (sq == HOUSE_OF_WATER) {
            return HOUSE_OF_REBIRTH; // Send to 15
        }
        return sq;
    }
}