import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // Initialize Board
        int[] board = new int[Board.BOARD_SIZE];
        for (int i = 0; i < 14; i++) {
            board[i] = (i % 2 == 0) ? Board.HUMAN : Board.AI; // 1, -1, 1, -1...
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Senet Game - Kendall's Rules");
        System.out.print("Enter search depth for AI (e.g., 4): ");
        int maxDepth = scanner.nextInt();
        System.out.print("Show AI debug info? (1=Yes, 0=No): ");
        boolean debug = scanner.nextInt() == 1;

        boolean isAiTurn = false; // Human starts (usually)

        while (! Rules.hasWon(board, Board.HUMAN) && !Rules.hasWon(board,Board.AI)) {
           Board.printBoard(board);
            int roll = Rules.throwSticks();
            String playerStr = isAiTurn ? "AI" : "Human";
            System.out.println(playerStr + " rolled: " + roll);

            List<int[]> moves =Rules.getPossibleMoves(board, isAiTurn ? Board.AI : Board.HUMAN, roll);

            if (moves.isEmpty()) {
                System.out.println("No moves available. Turn passed.");
            } else {
                if (isAiTurn) {
                    // AI TURN
                    System.out.println("AI is thinking...");
                   Rules.nodesVisited = 0;
                    double bestValue = Double.NEGATIVE_INFINITY;
                    int[] bestMove = moves.get(0);

                    for (int[] move : moves) {
                        // We check the value of the resulting state.
                        // The next step in recursion is Human's Chance Node (roll=0)
                        double val =Rules. expectiminimaxRecursive(move, maxDepth, false, 0);
                        
                        if (debug) System.out.println("Move Eval: " + val);
                        
                        if (val > bestValue) {
                            bestValue = val;
                            bestMove = move;
                        }
                    }
                    board = bestMove;
                    if (debug) System.out.println("AI chose node with value: " + bestValue + ". Nodes visited: " + Rules.nodesVisited);

                } else {
                    // HUMAN TURN
                    System.out.println("Possible moves:");
                    for (int i = 0; i < moves.size(); i++) {
                        // Find which piece moved to visualize
                        // Simple diff check for display logic would go here
                        System.out.println((i + 1) + ": Move piece.");
                    }
                    System.out.print("Select move (1-" + moves.size() + "): ");
                    int choice = scanner.nextInt();
                    if (choice >= 1 && choice <= moves.size()) {
                        board = moves.get(choice - 1);
                    }
                }
            }
            
            // Switch Turn
            isAiTurn = !isAiTurn;
        
        }
    
       Board.printBoard(board);
        if (Rules.hasWon(board, Board.AI)) System.out.println("AI Wins!");
        else System.out.println("Human Wins!");
    }

}
