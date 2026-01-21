
import java.util.List;

public class AI {

    private static final double P_1 = 0.25;
    private static final double P_2 = 0.375;
    private static final double P_3 = 0.25;
    private static final double P_4 = 0.0625;
    private static final double P_5 = 0.0625;

    private int nodesVisited = 0;

    private double evaluate(int[] board, int player) {
        double score = 0;

        for (int i = 0; i < board.length; i++) {
            if (board[i] == player) {
                score += (i + 1);
                if (Rules.isProtected(i + 1))
                    score += 5;
            } else if (board[i] == -player) {
                score -= (i + 1);
                if (Rules.isProtected(i + 1))
                    score -= 5;
            }
        }
        return score;
    }

    public Move getBestMove(int[] board, int diceRoll, int depth) {

        nodesVisited = 0;
        List<Move> moves = Rules.getPossibleMoves(board, Board.AI, diceRoll);

        if (moves.isEmpty())
            return null;

        Move bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Move move : moves) {
            int[] newBoard = board.clone();

            Board.applyMove(newBoard, move, Board.AI, false);

            double val = expectiminimax(newBoard, depth - 1, false);

            if (val > bestValue) {
                bestValue = val;
                bestMove = move;
            }
        }

        System.out.println("AI Search: " + nodesVisited +
                " nodes visited. Eval: " + bestValue);

        return bestMove;
    }

    private double expectiminimax(int[] board, int depth, boolean isMax) {

        nodesVisited++;

        if (depth == 0) {
            return evaluate(board, Board.AI);
        }

        double total = 0;

        total += P_1 * getBestMoveValue(board, 1, depth, isMax);
        total += P_2 * getBestMoveValue(board, 2, depth, isMax);
        total += P_3 * getBestMoveValue(board, 3, depth, isMax);
        total += P_4 * getBestMoveValue(board, 4, depth, isMax);
        total += P_5 * getBestMoveValue(board, 5, depth, isMax);

        return total;
    }

    private double getBestMoveValue(int[] board, int roll, int depth, boolean isMax) {

        int player = isMax ? Board.AI : Board.HUMAN;
        List<Move> moves = Rules.getPossibleMoves(board, player, roll);

        if (moves.isEmpty()) {
            return expectiminimax(board, depth - 1, !isMax);
        }

        double best = isMax
                ? Double.NEGATIVE_INFINITY
                : Double.POSITIVE_INFINITY;

        for (Move move : moves) {
            int[] nextBoard = board.clone();

            Board.applyMove(nextBoard, move, player, false);

            double val = expectiminimax(nextBoard, depth - 1, !isMax);

            best = isMax
                    ? Math.max(best, val)
                    : Math.min(best, val);
        }

        return best;
    }
}
