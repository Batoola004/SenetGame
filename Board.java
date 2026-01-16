public class Board {

    private static final int BOARD_SIZE = 30;
    private static final int HUMAN = 1;
    private static final int AI = -1;
    private static final int EMPTY = 0;

    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String WHITE = "\u001B[37m";
    private static Throw sticksThrow;

    public static void main(String[] args) {

        testPrintBoard();
    }

    public static void testPrintBoard() {
        int[] demoBoard = new int[BOARD_SIZE];

        for (int i = 0; i < 14; i++) {
            demoBoard[i] = (i % 2 == 0) ? HUMAN : AI;
        }

        demoBoard[25] = HUMAN;
        demoBoard[26] = AI;
        demoBoard[28] = AI;
        demoBoard[29] = HUMAN;

        System.out.println(WHITE + "--- SENET BOARD TEST RENDER ---" + RESET);
        printBoard(demoBoard);
        System.out.println(WHITE + "Test Complete." + RESET);
    }

    public static void printBoard(int[] board) {

        System.out.println(CYAN + "╔" + "══════════════════════════════════════════════════════════════" + "╗" + RESET);

        renderRow(board, 0, 9, true);
        System.out.println(CYAN + "║\u2009 1 →   2 →   3 →   4 →   5 →   6 →   7 →   8 →   9 →   10    ║" + RESET);

        System.out
                .println(CYAN + "╠" + "══════════════════════════════════════════════════════════════" + "╣" + RESET);

        renderRow(board, 19, 10, false);
        System.out.println(CYAN + "║  20 ←  19 ←  18 ←  17 ←  16 ←  15 ←  14 ←  13 ←  12 ←  11    ║" + RESET);

        System.out.println(CYAN + "╠" + "══════════════════════════════════════════════════════════════" + "╣" + RESET);

        renderRow(board, 20, 29, true);
        System.out.println(CYAN + "║\u200A 21  → 22  → 23  → 24  → 25  → 26* → 27~ → 28! → 29# → 30$   ║" + RESET);

        System.out.println(CYAN + "╚" + "══════════════════════════════════════════════════════════════" + "╝" + RESET);

        System.out.println(YELLOW + " *House of Happiness | ~House of Water | ! 3-Truths | # Atum | $ Horus" + RESET);
        System.out.println(" Pieces: " + BLUE + "Human (♙)" + RESET + "  " + RED + "AI (♟)" + RESET + "\n");

        sticksThrow = new Throw();
        sticksThrow.makeThrow();

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
}