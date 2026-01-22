import java.util.List;

public class AI {
    private static final double P_1 = 0.25;
    private static final double P_2 = 0.375;
    private static final double P_3 = 0.25;
    private static final double P_4 = 0.0625;
    private static final double P_5 = 0.0625;

    private int nodesVisited = 0;
    private double lastEvaluation = 0.0;

    public Move getBestMove(int[] board, int diceRoll, int depth) {
        nodesVisited = 0;
        List<Move> moves = Rules.getPossibleMoves(board, Board.AI, diceRoll);

        if (moves.isEmpty()) {
            // System.out.println("DEBUG AI: No moves found for roll " + diceRoll);
            return null;
        }

        Move bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Move move : moves) {
            int[] newBoard = board.clone();
            Board.applyMove(newBoard, move, Board.AI, false);
            // System.out.println("DEBUG AI: Checking move " + move);
            double val = expectiminimaxRecursive(newBoard, depth - 1, false, 0);

            if (val > bestValue) {
                bestValue = val;
                bestMove = move;
            }
        }

        lastEvaluation = bestValue;
        // System.out.println("AI SEARCH LOG: Nodes=" + nodesVisited + " | BestVal=" +
        // bestValue);
        return bestMove;
    }

    public double getLastEvaluation() {
        return lastEvaluation;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    private double expectiminimaxRecursive(int[] board, int depth, boolean isMaxTurn, int fixedRoll) {
        nodesVisited++;

        if (hasWon(board, Board.AI))
            return 10000 + depth;
        if (hasWon(board, Board.HUMAN))
            return -10000 - depth;

        if (depth <= 0)
            return evaluate(board);

        if (fixedRoll == 0) {
            double weightedAverage = 0;
            weightedAverage += P_1 * expectiminimaxRecursive(board, depth, isMaxTurn, 1);
            weightedAverage += P_2 * expectiminimaxRecursive(board, depth, isMaxTurn, 2);
            weightedAverage += P_3 * expectiminimaxRecursive(board, depth, isMaxTurn, 3);
            weightedAverage += P_4 * expectiminimaxRecursive(board, depth, isMaxTurn, 4);
            weightedAverage += P_5 * expectiminimaxRecursive(board, depth, isMaxTurn, 5);
            return weightedAverage;
        }

        int player = isMaxTurn ? Board.AI : Board.HUMAN;
        List<Move> moves = Rules.getPossibleMoves(board, player, fixedRoll);

        if (moves.isEmpty()) {
            return expectiminimaxRecursive(board, depth - 1, !isMaxTurn, 0);
        }

        if (isMaxTurn) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for (Move move : moves) {
                int[] nextBoard = board.clone();
                Board.applyMove(nextBoard, move, player, false);
                maxEval = Math.max(maxEval, expectiminimaxRecursive(nextBoard, depth - 1, false, 0));
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for (Move move : moves) {
                int[] nextBoard = board.clone();
                Board.applyMove(nextBoard, move, player, false);
                minEval = Math.min(minEval, expectiminimaxRecursive(nextBoard, depth - 1, true, 0));
            }
            return minEval;
        }
    }

    private double evaluate(int[] board) {
        double score = 0;
        int aiPieces = 0;
        int humanPieces = 0;

        for (int i = 0; i < board.length; i++) {
            if (board[i] == Board.AI) {
                aiPieces++;
                score += (i + 1);
                if (i + 1 > 25)
                    score += 5;
                if (i + 1 >= 28)
                    score += 10;
            } else if (board[i] == Board.HUMAN) {
                humanPieces++;
                score -= (i + 1);
                if (i + 1 > 25)
                    score -= 5;
                if (i + 1 >= 28)
                    score -= 10;
            }
        }

        score += (5 - aiPieces) * 100;
        score -= (5 - humanPieces) * 100;

        return score;
    }

    private boolean hasWon(int[] board, int player) {
        for (int b : board) {
            if (b == player)
                return false;
        }
        return true;
    }
}