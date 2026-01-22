// import java.util.*;

// public class Rules {
//     public static final int BOARD_SIZE = 30;
//     public static final int HUMAN = 1;
//     public static final int AI = -1;
//     public static final int EMPTY = 0;

//     public static final int HOUSE_OF_REBIRTH = 15;
//     public static final int HOUSE_OF_HAPPINESS = 26;
//     public static final int HOUSE_OF_WATER = 27;
//     public static final int HOUSE_OF_THREE_TRUTHS = 28;
//     public static final int HOUSE_OF_RE_ATUM = 29;
//     public static final int HOUSE_OF_HORUS = 30;

//     public static List<Move> getPossibleMoves(int[] board, int player, int throwValue) {
//         List<Move> moves = new ArrayList<>();
//         // System.out.println("DEBUG RULES: Calculating moves for player " + player +
//         // "with roll " + throwValue);

//         for (int i = 0; i < BOARD_SIZE; i++) {
//             if (board[i] == player) {
//                 int fromSq = i + 1;
//                 int toSq = fromSq + throwValue;

//                 if (fromSq == HOUSE_OF_THREE_TRUTHS && throwValue == 3) {
//                     moves.add(new Move(fromSq, -1));
//                     continue;
//                 }
//                 if (fromSq == HOUSE_OF_RE_ATUM && throwValue == 2) {
//                     moves.add(new Move(fromSq, -1));
//                     continue;
//                 }
//                 if (fromSq == HOUSE_OF_HORUS && throwValue == 1) {
//                     moves.add(new Move(fromSq, -1));
//                     continue;
//                 }

//                 if (toSq > BOARD_SIZE) {
//                     if (fromSq > HOUSE_OF_HAPPINESS) {
//                         moves.add(new Move(fromSq, -1));
//                     }
//                     continue;
//                 }

//                 if (isValidDestination(board, player, toSq)) {
//                     moves.add(new Move(fromSq, toSq));
//                 }
//             }
//         }

//         if (throwValue >= 1 && throwValue <= 5) {
//             int count = 0;
//             for (int b : board) {
//                 if (b == player)
//                     count++;
//             }
//             if (count < 5 && board[0] != player) {
//                 if (isValidDestination(board, player, 1))
//                     moves.add(new Move(0, 1));
//             }
//         }

//         return moves;
//     }

//     private static boolean isValidDestination(int[] board, int player, int square) {
//         if (square < 1 || square > BOARD_SIZE)
//             return false;
//         int occupant = board[square - 1];
//         if (occupant == player)
//             return false;
//         if (occupant != EMPTY && isProtected(square))
//             return false;
//         return true;
//     }

//     public static boolean isProtected(int square) {
//         return square == HOUSE_OF_HAPPINESS || square == HOUSE_OF_THREE_TRUTHS ||
//                 square == HOUSE_OF_RE_ATUM || square == HOUSE_OF_HORUS || square == HOUSE_OF_REBIRTH;
//     }

//     public static int applySpecialSquareEffect(int landedSquare, int[] board) {
//         if (landedSquare == HOUSE_OF_WATER) {
//             // System.out.println("SPECIAL LOG: Water square triggered - searching for
//             // rebirth spot");
//             int rebirthIdx = HOUSE_OF_REBIRTH - 1;
//             while (rebirthIdx >= 0 && board[rebirthIdx] != EMPTY) {
//                 rebirthIdx--;
//             }
//             return (rebirthIdx >= 0) ? rebirthIdx + 1 : 1;
//         }
//         if (landedSquare == HOUSE_OF_HORUS) {
//             return -1;
//         }
//         return landedSquare;
//     }
// }

// import java.util.*;

// public class Rules {

//     public static final int HOUSE_OF_REBIRTH = 15;
//     public static final int HOUSE_OF_HAPPINESS = 26;
//     public static final int HOUSE_OF_WATER = 27;
//     public static final int HOUSE_OF_THREE_TRUTHS = 28;
//     public static final int HOUSE_OF_RE_ATUM = 29;
//     public static final int HOUSE_OF_HORUS = 30;

//     public static List<Move> getPossibleMoves(int[] board, int player, int roll) {
//         List<Move> moves = new ArrayList<>();

//         for (int i = 0; i < board.length; i++) {
//             if (board[i] != player)
//                 continue;

//             int from = i + 1;
//             int to = from + roll;

//             if (from < 26 && to > 26 && to != 26)
//                 continue;

//             if (from == 28 && roll == 3 ||
//                     from == 29 && roll == 2 ||
//                     from == 30 && roll == 1) {
//                 moves.add(new Move(from, -1));
//                 continue;
//             }

//             if (to > 30)
//                 continue;

//             if (isValid(board, player, to))
//                 moves.add(new Move(from, to));
//         }

//         return moves;
//     }

//     private static boolean isValid(int[] board, int player, int sq) {
//         int o = board[sq - 1];
//         return o == 0 || (o != player && !isProtected(sq));
//     }

//     public static boolean isProtected(int sq) {
//         return sq == 26 || sq == 28 || sq == 29 || sq == 30;
//     }

//     public static int applySpecialSquareEffect(int sq, int[] board) {
//         if (sq == 27) {
//             for (int i = 14; i >= 0; i--)
//                 if (board[i] == 0)
//                     return i + 1;
//             return -1;
//         }

//         if (sq == 28 || sq == 29 || sq == 30) {
//             return HOUSE_OF_REBIRTH;
//         }

//         return sq;
//     }
// }
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

            // Rule: Cannot jump over House of Happiness (26)
            if (from < HOUSE_OF_HAPPINESS && to > HOUSE_OF_HAPPINESS && to != HOUSE_OF_HAPPINESS)
                continue;

            // Rule: Exiting the board from special squares
            // House of Three Truths (28) exits on roll 3
            if (from == HOUSE_OF_THREE_TRUTHS && roll == 3) {
                moves.add(new Move(from, -1));
                continue;
            }
            // House of Re-Atoum (29) exits on roll 2
            if (from == HOUSE_OF_RE_ATUM && roll == 2) {
                moves.add(new Move(from, -1));
                continue;
            }
            // House of Horus (30) exits on roll 1
            if (from == HOUSE_OF_HORUS && roll == 1) {
                moves.add(new Move(from, -1));
                continue;
            }

            // Rule: Cannot move past the end of the board (30) unless exiting
            if (to > HOUSE_OF_HORUS) {
                // A piece can exit the board if it is past House of Happiness (26)
                if (from > HOUSE_OF_HAPPINESS) {
                    moves.add(new Move(from, -1));
                }
                continue;
            }

            // Check if the destination square is valid
            if (isValid(board, player, to))
                moves.add(new Move(from, to));
        }

        return moves;
    }

    private static boolean isValid(int[] board, int player, int sq) {
        int o = board[sq - 1];
        // Valid if square is empty (o == 0) OR
        // if square is occupied by opponent (o != player) AND it is NOT protected.
        return o == 0 || (o != player && !isProtected(sq));
    }

    public static boolean isProtected(int sq) {
        // Protected squares are 26, 28, 29, 30. House of Rebirth (15) is also
        // protected.
        return sq == HOUSE_OF_HAPPINESS || sq == HOUSE_OF_THREE_TRUTHS ||
                sq == HOUSE_OF_RE_ATUM || sq == HOUSE_OF_HORUS || sq == HOUSE_OF_REBIRTH;
    }

    /**
     * Applies the immediate effect of landing on a special square.
     * Returns the new square number, or the original square if no effect, or -1 if
     * the piece exits.
     */
    public static int applySpecialSquareEffect(int sq, int[] board) {
        // House of Water (27): Piece is immediately returned to the first empty square
        // from 15 backwards. If none are empty, it is removed from the board (handled
        // by caller).
        if (sq == HOUSE_OF_WATER) {
            // Search for the first empty square from 15 backwards (index 14 to 0)
            for (int i = HOUSE_OF_REBIRTH - 1; i >= 0; i--) {
                if (board[i] == 0) {
                    return i + 1; // Return the square number
                }
            }
            return -1; // No empty square found, piece is removed from board
        }

        // Squares 28, 29, 30 have no immediate effect on landing.
        // Their special rules are for exiting the board, which is handled in
        // getPossibleMoves.
        // House of Rebirth (15) has no immediate effect on landing.

        return sq;
    }
}