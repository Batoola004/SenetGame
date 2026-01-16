public class Board {

    // 0 = Empty, 1 = Human, -1 = Computer
    public static final int EMPTY = 0;
    public static final int HUMAN = 1;
    public static final int AI = -1;
    
    // Board size
    public static final int BOARD_SIZE = 30;
    // Helper to print the board in S-shape
    public static void printBoard(int[] board) {
        System.out.println("--------------------------------------------------");
        
        // Row 1: Left to Right (1-10) -> Indices 0-9
        for (int i = 0; i < 10; i++) System.out.print(getSymbol(board[i]) + "\t");
        System.out.println("\n(1) -> -> -> -> -> -> -> -> -> (10)");
        
        // Row 2: Right to Left (20-11) -> Indices 19-10
        for (int i = 19; i >= 10; i--) System.out.print(getSymbol(board[i]) + "\t");
        System.out.println("\n(20) <- <- <- <- <- <- <- <- <- (11)");

        // Row 3: Left to Right (21-30) -> Indices 20-29
        for (int i = 20; i < 30; i++) System.out.print(getSymbol(board[i]) + "\t");
        System.out.println("\n(21) -> -> -> -> -> -> -> -> -> (30)");
        System.out.println("--------------------------------------------------");
    }

    private static String getSymbol(int val) {
        if (val == HUMAN) return "O"; // White/Human
        if (val == AI) return "X";    // Black/AI
        return ".";
    }
}