import java.util.*;

public class Rules {
    public static final int BOARD_SIZE = 30;
    public static final int HUMAN = 1;
    public static final int AI = -1;
    public static final int EMPTY = 0;

    public static final int HOUSE_OF_REBIRTH = 15;
    public static final int HOUSE_OF_HAPPINESS = 26;
    public static final int HOUSE_OF_WATER = 27;
    public static final int HOUSE_OF_THREE_TRUTHS = 28;
    public static final int HOUSE_OF_RE_ATUM = 29;
    public static final int HOUSE_OF_HORUS = 30;

    public static List<Move> getPossibleMoves(int[] board, int player, int throwValue) {
        List<Move> moves = new ArrayList<>();
        // System.out.println("DEBUG RULES: Calculating moves for player " + player +
        // "with roll " + throwValue);

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i] == player) {
                int fromSq = i + 1;
                int toSq = fromSq + throwValue;

                if (fromSq == HOUSE_OF_THREE_TRUTHS && throwValue == 3) {
                    moves.add(new Move(fromSq, -1));
                    continue;
                }
                if (fromSq == HOUSE_OF_RE_ATUM && throwValue == 2) {
                    moves.add(new Move(fromSq, -1));
                    continue;
                }
                if (fromSq == HOUSE_OF_HORUS && throwValue == 1) {
                    moves.add(new Move(fromSq, -1));
                    continue;
                }

                if (toSq > BOARD_SIZE) {
                    if (fromSq > HOUSE_OF_HAPPINESS) {
                        moves.add(new Move(fromSq, -1));
                    }
                    continue;
                }

                if (isValidDestination(board, player, toSq)) {
                    moves.add(new Move(fromSq, toSq));
                }
            }
        }

        if (throwValue >= 1 && throwValue <= 5) {
            int count = 0;
            for (int b : board) {
                if (b == player)
                    count++;
            }
            if (count < 5 && board[0] != player) {
                if (isValidDestination(board, player, 1))
                    moves.add(new Move(0, 1));
            }
        }

        return moves;
    }

    private static boolean isValidDestination(int[] board, int player, int square) {
        if (square < 1 || square > BOARD_SIZE)
            return false;
        int occupant = board[square - 1];
        if (occupant == player)
            return false;
        if (occupant != EMPTY && isProtected(square))
            return false;
        return true;
    }

    public static boolean isProtected(int square) {
        return square == HOUSE_OF_HAPPINESS || square == HOUSE_OF_THREE_TRUTHS ||
                square == HOUSE_OF_RE_ATUM || square == HOUSE_OF_HORUS || square == HOUSE_OF_REBIRTH;
    }

    public static int applySpecialSquareEffect(int landedSquare, int[] board) {
        if (landedSquare == HOUSE_OF_WATER) {
            // System.out.println("SPECIAL LOG: Water square triggered - searching for
            // rebirth spot");
            int rebirthIdx = HOUSE_OF_REBIRTH - 1;
            while (rebirthIdx >= 0 && board[rebirthIdx] != EMPTY) {
                rebirthIdx--;
            }
            return (rebirthIdx >= 0) ? rebirthIdx + 1 : 1;
        }
        if (landedSquare == HOUSE_OF_HORUS) {
            return -1;
        }
        return landedSquare;
    }
}