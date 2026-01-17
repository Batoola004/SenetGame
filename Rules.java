
// Rules.java
import java.util.*;

public class Rules {

    public static final int BOARD_SIZE = 30;
    public static final int HUMAN = 1;
    public static final int AI = -1;
    public static final int EMPTY = 0;

    public static final int HOUSE_OF_REBIRTH = 15; // Safe square
    public static final int HOUSE_OF_HAPPINESS = 26; // Must proceed forward
    public static final int HOUSE_OF_WATER = 27; // Drown → go back to 15
    public static final int HOUSE_OF_THREE_TRUTHS = 28; // Must roll exactly 3 to leave
    public static final int HOUSE_OF_RE_ATUM = 29; // Must roll exactly 2 to leave
    public static final int HOUSE_OF_HORUS = 30; // Final square — exit on exact roll

    public static List<Move> getPossibleMoves(int[] board, int player, int throwValue) {
        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i] == player) {
                int fromSquare = i + 1;
                int toSquare = fromSquare + throwValue;

                if (toSquare > BOARD_SIZE)
                    continue; // Cannot overshoot

                if (isValidDestination(board, player, toSquare)) {
                    moves.add(new Move(fromSquare, toSquare));
                }
            }
        }

        if (throwValue >= 1 && throwValue <= 5) {

            if (board[0] != player) {
                if (isValidDestination(board, player, 1)) {
                    moves.add(new Move(0, 1));
                }
            }
        }

        return moves;
    }

    private static boolean isValidDestination(int[] board, int player, int square) {
        if (square < 1 || square > BOARD_SIZE)
            return false;

        int idx = square - 1;
        int occupant = board[idx];

        if (occupant == player)
            return false;

        if (occupant != EMPTY && isProtected(square)) {
            return false;
        }

        return true;
    }

    public static boolean isProtected(int square) {
        return square == HOUSE_OF_HAPPINESS ||
                square == HOUSE_OF_THREE_TRUTHS ||
                square == HOUSE_OF_RE_ATUM ||
                square == HOUSE_OF_HORUS;
    }

    public static int applySpecialSquareEffect(int landedSquare) {
        switch (landedSquare) {
            case HOUSE_OF_WATER:
                System.out.println("💦 Landing on House of Water! Moving back to House of Rebirth (Square 15)");
                return HOUSE_OF_REBIRTH; // Drown → rebirth

            case HOUSE_OF_HORUS:
                System.out.println("🌅 Landing on House of Horus! Piece exits the board!");
                return -1; // Exit board

            default:
                return landedSquare;
        }
    }

    public static boolean canExitFrom(int square, int roll) {
        if (square == HOUSE_OF_THREE_TRUTHS)
            return roll == 3;
        if (square == HOUSE_OF_RE_ATUM)
            return roll == 2;
        if (square == HOUSE_OF_HORUS)
            return roll == 1;
        return false;
    }
}