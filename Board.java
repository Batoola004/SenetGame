import java.util.*;

public class Board {
    public static final int BOARD_SIZE = 30;
    public static final int HUMAN = 1;
    public static final int AI = -1;
    public static final int EMPTY = 0;

    // ANSI color codes for console display
    public static final String RESET = "\u001B[0m";
    public static final String BLUE = "\u001B[34m";
    public static final String RED = "\u001B[31m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String WHITE = "\u001B[37m";
    public static final String GREEN = "\u001B[32m"; // Added for win messages

    public static void printBoard(int[] board) {
        System.out.println(CYAN + "╔══════════════════════════════════════════════════════════════╗" + RESET);
        renderRow(board, 0, 9, true);
        System.out.println(CYAN + "║\u2009 1 →   2 →   3 →   4 →   5 →   6 →   7 →   8 →   9 →   10    ║" + RESET);
        System.out.println(CYAN + "╠══════════════════════════════════════════════════════════════╣" + RESET);
        renderRow(board, 19, 10, false);
        System.out.println(CYAN + "║  20 ←  19 ←  18 ←  17 ←  16 ←  15 ←  14 ←  13 ←  12 ←  11    ║" + RESET);
        System.out.println(CYAN + "╠══════════════════════════════════════════════════════════════╣" + RESET);
        renderRow(board, 20, 29, true);
        System.out.println(CYAN + "║\u2009 21 →  22 →  23 →  24 → 25 →  26* → 27~ → 28! → 29# → 30$    ║" + RESET);
        System.out.println(CYAN + "╚══════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println(YELLOW + " *Happiness | ~Water | !3-Truths | #Atum | $Horus" + RESET);
        System.out.println(" Pieces: " + BLUE + "Human (♙)" + RESET + "  " + RED + "AI (♟)" + RESET + "\n");
    }

    private static void renderRow(int[] board, int start, int end, boolean leftToRight) {
        System.out.print(CYAN + "║ " + RESET);
        if (leftToRight) {
            for (int i = start; i <= end; i++) {
                System.out.print(" " + getSymbol(board[i]) + "    ");
            }
        } else {
            for (int i = start; i >= end; i--) {
                System.out.print(" " + getSymbol(board[i]) + "    ");
            }
        }
        System.out.println(CYAN + " ║" + RESET);
    }

    private static String getSymbol(int value) {
        if (value == HUMAN)
            return BLUE + "♙" + RESET;
        if (value == AI)
            return RED + "♟" + RESET;
        return ".";
    }

    public static void applyMove(int[] board, Move move, int player, boolean verbose) {
        int from = move.from - 1; // Convert to 0-based index
        int to = move.to - 1; // Convert to 0-based index

        // Handle moving from off-board position
        if (move.from == 0) {
            // New piece entering the board
            if (board[0] == EMPTY) {
                board[0] = player;
                return;
            }
        }

        // Basic move logic
        if (from >= 0 && from < BOARD_SIZE && board[from] == player) {
            // Check if we're moving to an occupied square
            if (to >= 0 && to < BOARD_SIZE && board[to] != EMPTY) {
                // Handle piece swapping according to Senet rules
                if (!Rules.isProtected(to + 1)) {
                    // Swap positions instead of capturing
                    int temp = board[to];
                    board[to] = player;
                    board[from] = temp;

                    if (verbose) {
                        System.out.println("↪ Pieces swapped positions!");
                    }
                } else {
                    // Protected square - cannot land here
                    if (verbose) {
                        System.out.println("❌ Cannot land on protected square!");
                    }
                    return;
                }
            } else {
                // Normal move to empty square
                board[from] = EMPTY;
                if (to >= 0 && to < BOARD_SIZE) {
                    board[to] = player;
                }
            }
        }

        // Handle special square effects
        if (to >= 0 && to < BOARD_SIZE) {
            int result = Rules.applySpecialSquareEffect(to + 1, board); // Pass board for rebirth logic

            if (result == -1) {
                // Piece exits the board
                board[to] = EMPTY;
                if (verbose) {
                    System.out.println(player == HUMAN
                            ? "🎉 Human piece exited!"
                            : "🎉 AI piece exited!");
                }
            } else if (result != to + 1) {
                // Piece moved to a different square due to special effect
                board[to] = EMPTY;
                int newIndex = result - 1;

                // Check if the target square is occupied
                if (newIndex >= 0 && newIndex < BOARD_SIZE && board[newIndex] != EMPTY) {
                    if (verbose) {
                        System.out.println("Target square " + result + " is occupied!");
                    }

                    // Find first empty square before the target
                    int searchIndex = newIndex - 1;
                    while (searchIndex >= 0 && board[searchIndex] != EMPTY) {
                        searchIndex--;
                    }

                    if (searchIndex >= 0) {
                        if (verbose) {
                            System.out.println("Moving to first empty square at position " + (searchIndex + 1));
                        }
                        board[searchIndex] = player;
                    } else {
                        // No empty squares found - this shouldn't happen in normal play
                        if (verbose) {
                            System.out.println("⚠️ No empty squares found! Placing at position 1");
                        }
                        board[0] = player;
                    }
                } else {
                    // Target square is empty
                    board[newIndex] = player;
                }

                if (verbose) {
                    System.out.println("↪ Piece moved to square " + result);
                }
            }
        }
    }

    public static void testPrintBoard() {
        int[] demo = new int[BOARD_SIZE];
        for (int i = 0; i < 14; i++) {
            demo[i] = (i % 2 == 0) ? HUMAN : AI;
        }
        demo[25] = HUMAN;
        demo[26] = AI;
        demo[28] = AI;
        demo[29] = HUMAN;
        printBoard(demo);
    }
}