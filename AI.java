import java.util.List;

public class AI {
    // Dice roll probabilities for 4 sticks (Kendall's rules)
    private static final double P_1 = 0.25; // 1 stick showing dark
    private static final double P_2 = 0.375; // 2 sticks showing dark
    private static final double P_3 = 0.25; // 3 sticks showing dark
    private static final double P_4 = 0.0625; // 4 sticks showing dark
    private static final double P_5 = 0.0625; // 0 sticks showing dark (counts as 5)

    private int nodesVisited = 0;
    private double lastEvaluation = 0.0;

    public Move getBestMove(int[] board, int diceRoll, int depth) {
        nodesVisited = 0;
        List<Move> moves = Rules.getPossibleMoves(board, Board.AI, diceRoll);

        if (moves.isEmpty()) {
            System.out.println("DEBUG: No valid moves available for AI");
            return null;
        }

        Move bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Move move : moves) {
            int[] newBoard = board.clone();
            Board.applyMove(newBoard, move, Board.AI, false);
            double val = expectiminimaxRecursive(newBoard, depth - 1, false, 0);

            if (val > bestValue) {
                bestValue = val;
                bestMove = move;
            }
        }

        lastEvaluation = bestValue;
        System.out.println("AI Search: " + nodesVisited + " nodes visited. Eval: " + bestValue);
        return bestMove;
    }

    public double getLastEvaluation() {
        return lastEvaluation;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    // Main expectiminimax function with proper chance nodes
    private double expectiminimaxRecursive(int[] board, int depth, boolean isMaxTurn, int fixedRoll) {
        nodesVisited++;

        // Check for terminal states first
        if (hasWon(board, Board.AI)) {
            return 10000 + depth; // Prefer faster wins
        }
        if (hasWon(board, Board.HUMAN)) {
            return -10000 - depth; // Prefer slower losses
        }

        if (depth <= 0) {
            return evaluate(board);
        }

        // Chance node - we need to roll the dice
        if (fixedRoll == 0) {
            double weightedAverage = 0;

            // Try all possible dice rolls with their probabilities
            weightedAverage += P_1 * expectiminimaxRecursive(board, depth, isMaxTurn, 1);
            weightedAverage += P_2 * expectiminimaxRecursive(board, depth, isMaxTurn, 2);
            weightedAverage += P_3 * expectiminimaxRecursive(board, depth, isMaxTurn, 3);
            weightedAverage += P_4 * expectiminimaxRecursive(board, depth, isMaxTurn, 4);
            weightedAverage += P_5 * expectiminimaxRecursive(board, depth, isMaxTurn, 5);

            return weightedAverage;
        }

        // Decision node - generate moves for current player
        int player = isMaxTurn ? Board.AI : Board.HUMAN;
        List<Move> moves = Rules.getPossibleMoves(board, player, fixedRoll);

        // If no moves available, pass turn
        if (moves.isEmpty()) {
            return expectiminimaxRecursive(board, depth - 1, !isMaxTurn, 0);
        }

        if (isMaxTurn) {
            // MAX node (AI's turn)
            double maxEval = Double.NEGATIVE_INFINITY;

            for (Move move : moves) {
                int[] nextBoard = board.clone();
                Board.applyMove(nextBoard, move, player, false);
                double eval = expectiminimaxRecursive(nextBoard, depth - 1, false, 0);

                if (eval > maxEval) {
                    maxEval = eval;
                }
            }
            return maxEval;
        } else {
            // MIN node (Human's turn)
            double minEval = Double.POSITIVE_INFINITY;

            for (Move move : moves) {
                int[] nextBoard = board.clone();
                Board.applyMove(nextBoard, move, player, false);
                double eval = expectiminimaxRecursive(nextBoard, depth - 1, true, 0);

                if (eval < minEval) {
                    minEval = eval;
                }
            }
            return minEval;
        }
    }

    // Heuristic evaluation function with bearing off bonuses
    private double evaluate(int[] board) {
        double score = 0;
        int aiPieces = 0;
        int humanPieces = 0;

        for (int i = 0; i < board.length; i++) {
            if (board[i] == Board.AI) {
                aiPieces++;
                score += (i + 1); // Position advancement

                // Bonus for special squares
                if (i + 1 > 25) {
                    score += 5;
                }

                // Extra bonus for being close to exit
                if (i + 1 >= 28) {
                    score += 10;
                }
            } else if (board[i] == Board.HUMAN) {
                humanPieces++;
                score -= (i + 1);

                if (i + 1 > 25) {
                    score -= 5;
                }

                if (i + 1 >= 28) {
                    score -= 10;
                }
            }
        }

        // HUGE bonuses for bearing off pieces - this is the main goal
        score += (5 - aiPieces) * 100; // AI wants to bear off pieces
        score -= (5 - humanPieces) * 100; // AI wants to prevent human from bearing off

        return score;
    }

    private boolean hasWon(int[] board, int player) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == player) {
                return false;
            }
        }
        return true;
    }
}