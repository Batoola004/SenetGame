import java.util.*;

public class Rules {
    public static final int BOARD_SIZE = 30;
    public static final int HUMAN = 1;
    public static final int AI = -1;
    public static final int EMPTY = 0;

    // Special square definitions
    public static final int HOUSE_OF_REBIRTH = 15; // Safe square
    public static final int HOUSE_OF_HAPPINESS = 26; // Must proceed forward exactly
    public static final int HOUSE_OF_WATER = 27; // Drown → go back to rebirth
    public static final int HOUSE_OF_THREE_TRUTHS = 28; // Must roll exactly 3 to leave
    public static final int HOUSE_OF_RE_ATUM = 29; // Must roll exactly 2 to leave
    public static final int HOUSE_OF_HORUS = 30; // Final square — exit on exact roll

    public static List<Move> getPossibleMoves(int[] board, int player, int throwValue) {
        List<Move> moves = new ArrayList<>();

        // Try moving existing pieces on the board
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i] == player) {
                int fromSquare = i + 1;
                int toSquare = fromSquare + throwValue;

                // Handle bearing off from special squares
                if (fromSquare == HOUSE_OF_THREE_TRUTHS && throwValue == 3) {
                    moves.add(new Move(fromSquare, -1)); // Exit the board
                    continue;
                }
                if (fromSquare == HOUSE_OF_RE_ATUM && throwValue == 2) {
                    moves.add(new Move(fromSquare, -1)); // Exit the board
                    continue;
                }
                if (fromSquare == HOUSE_OF_HORUS && throwValue == 1) {
                    moves.add(new Move(fromSquare, -1)); // Exit the board
                    continue;
                }

                // Handle normal moves
                if (toSquare > BOARD_SIZE) {
                    // Can only bear off if we're past house of happiness and have exact roll
                    if (fromSquare > HOUSE_OF_HAPPINESS) {
                        moves.add(new Move(fromSquare, -1)); // Exit the board
                    }
                    continue;
                }

                if (isValidDestination(board, player, toSquare)) {
                    moves.add(new Move(fromSquare, toSquare));
                }
            }
        }

        // Handle entering new pieces from off-board
        if (throwValue >= 1 && throwValue <= 5) {
            // Count how many pieces this player has on the board
            int playerPieces = 0;
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i] == player) {
                    playerPieces++;
                }
            }

            // Can only enter new piece if we have less than 5 pieces total on board
            if (playerPieces < 5 && board[0] != player) {
                if (isValidDestination(board, player, 1)) {
                    moves.add(new Move(0, 1)); // From off-board to square 1
                }
            }
        }

        return moves;
    }

    private static boolean isValidDestination(int[] board, int player, int square) {
        if (square < 1 || square > BOARD_SIZE) {
            return false;
        }

        int idx = square - 1;
        int occupant = board[idx];

        // Cannot land on own piece
        if (occupant == player) {
            return false;
        }

        // Protected squares cannot be landed on if occupied by opponent
        if (occupant != EMPTY && isProtected(square)) {
            return false;
        }

        // Special rule for House of Happiness - must land exactly
        if (square == HOUSE_OF_HAPPINESS) {
            return true; // Always valid if empty or swappable
        }

        return true;
    }

    public static boolean isProtected(int square) {
        return square == HOUSE_OF_HAPPINESS ||
                square == HOUSE_OF_THREE_TRUTHS ||
                square == HOUSE_OF_RE_ATUM ||
                square == HOUSE_OF_HORUS;
    }

    public static int applySpecialSquareEffect(int landedSquare, int[] board) {
        switch (landedSquare) {
            case HOUSE_OF_WATER:
                System.out.println("💦 Landing on House of Water! Moving back to House of Rebirth (Square 15)");

                // Find first empty square starting from House of Rebirth (15) going backwards
                int rebirthIndex = HOUSE_OF_REBIRTH - 1; // Convert to 0-based

                while (rebirthIndex >= 0 && board[rebirthIndex] != EMPTY) {
                    rebirthIndex--;
                }

                if (rebirthIndex >= 0) {
                    return rebirthIndex + 1; // Convert back to 1-based
                } else {
                    // No empty squares found - should never happen in normal play
                    System.out.println("⚠️ No empty squares found before House of Rebirth! Placing at square 1");
                    return 1;
                }

            case HOUSE_OF_HORUS:
                System.out.println("🌅 Landing on House of Horus! Piece exits the board!");
                return -1; // Exit board

            default:
                return landedSquare;
        }
    }

    public static boolean canExitFrom(int square, int roll) {
        if (square == HOUSE_OF_THREE_TRUTHS) {
            return roll == 3;
        }
        if (square == HOUSE_OF_RE_ATUM) {
            return roll == 2;
        }
        if (square == HOUSE_OF_HORUS) {
            return roll == 1;
        }
        return false;
    }
}