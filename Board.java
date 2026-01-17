import java.util.*;

public class Board {

    public static final int BOARD_SIZE = 30;
    public static final int HUMAN = 1;
    public static final int AI = -1;
    public static final int EMPTY = 0;

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

        int from = move.from;
        int to = move.to;

        board[from] = EMPTY;

        if (to == -1) {
            if (verbose) {
                System.out.println(player == HUMAN
                        ? "🎉 Human piece exited!"
                        : "🎉 AI piece exited!");
            }
            return;
        }

        if (board[to] != EMPTY && verbose) {
            System.out.println(board[to] == HUMAN
                    ? "Human piece captured!"
                    : "AI piece captured!");
        }

        board[to] = player;

        int result = Rules.applySpecialSquareEffect(to);

        if (result == -1) {
            board[to] = EMPTY;
            if (verbose) {
                System.out.println(player == HUMAN
                        ? "🎉 Human piece exited via Horus!"
                        : "🎉 AI piece exited via Horus!");
            }
        }

        else if (result != to) {
            board[to] = EMPTY;

            if (board[result] != EMPTY && verbose) {
                System.out.println(board[result] == HUMAN
                        ? "Human piece captured!"
                        : "AI piece captured!");
            }

            board[result] = player;

            if (verbose) {
                System.out.println("↪ Piece moved to square " + (result + 1));
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

    // ======================
    // MAIN METHOD FOR TESTING
    // ======================

    // public static void main(String[] args) {
    // System.out.println(WHITE + "🔍 Running board rendering test..." + RESET);
    // testPrintBoard();
    // System.out.println(GREEN + "✅ Board rendering test complete!" + RESET);
    // }
}