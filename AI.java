
// import java.util.List;

// public class AI {

//     private static final double P_1 = 0.25;
//     private static final double P_2 = 0.375;
//     private static final double P_3 = 0.25;
//     private static final double P_4 = 0.0625;
//     private static final double P_5 = 0.0625;

//     private int nodesVisited = 0;

//     public Move getBestMove(int[] board, int depth) {
//         nodesVisited = 0;
//         Move bestMove = null;
//         double bestValue = Double.NEGATIVE_INFINITY;

//         List<Move> moves = Rules.getPossibleMoves(board, Board.AI, 0);

//         for (Move move : moves) {
//             int[] next = board.clone();
//             Board.applyMove(next, move, Board.AI);

//             double val = expectiminimax(
//                     next,
//                     depth - 1,
//                     false,
//                     0,
//                     Double.NEGATIVE_INFINITY,
//                     Double.POSITIVE_INFINITY);

//             if (val > bestValue) {
//                 bestValue = val;
//                 bestMove = move;
//             }
//         }

//         return bestMove;
//     }

//     private double expectiminimax(
//             int[] board,
//             int depth,
//             boolean isMax,
//             int fixedRoll,
//             double alpha,
//             double beta) {
//         nodesVisited++;

//         if (hasWon(board, Board.AI))
//             return 10000 + depth;
//         if (hasWon(board, Board.HUMAN))
//             return -10000 - depth;

//         if (depth == 0)
//             return evaluate(board);

//         /* ---------- CHANCE NODE ---------- */
//         if (fixedRoll == 0) {
//             return P_1 * expectiminimax(board, depth - 1, isMax, 1, alpha, beta) +
//                     P_2 * expectiminimax(board, depth - 1, isMax, 2, alpha, beta) +
//                     P_3 * expectiminimax(board, depth - 1, isMax, 3, alpha, beta) +
//                     P_4 * expectiminimax(board, depth - 1, isMax, 4, alpha, beta) +
//                     P_5 * expectiminimax(board, depth - 1, isMax, 5, alpha, beta);
//         }

//         int player = isMax ? Board.AI : Board.HUMAN;
//         List<Move> moves = Rules.getPossibleMoves(board, player, fixedRoll);

//         if (moves.isEmpty()) {
//             return expectiminimax(board, depth - 1, !isMax, 0, alpha, beta);
//         }

//         if (isMax) {
//             double value = Double.NEGATIVE_INFINITY;

//             for (Move m : moves) {
//                 int[] next = board.clone();
//                 Board.applyMove(next, m, player);

//                 value = Math.max(value,
//                         expectiminimax(next, depth - 1, false, 0, alpha, beta));

//                 alpha = Math.max(alpha, value);
//                 if (beta <= alpha)
//                     break; // PRUNE
//             }
//             return value;
//         }

//         else {
//             double value = Double.POSITIVE_INFINITY;

//             for (Move m : moves) {
//                 int[] next = board.clone();
//                 Board.applyMove(next, m, player);

//                 value = Math.min(value,
//                         expectiminimax(next, depth - 1, true, 0, alpha, beta));

//                 beta = Math.min(beta, value);
//                 if (beta <= alpha)
//                     break; // PRUNE
//             }
//             return value;
//         }
//     }

//     private boolean hasWon(int[] board, int player) {
//         for (int b : board)
//             if (b == player)
//                 return false;
//         return true;
//     }

//     private double evaluate(int[] board) {
//         double score = 0;

//         for (int i = 0; i < board.length; i++) {
//             if (board[i] == Board.AI) {
//                 score += i + 1;
//                 if (i + 1 == 27)
//                     score -= 30;
//                 if (i + 1 >= 28)
//                     score += 20;
//             }
//             if (board[i] == Board.HUMAN) {
//                 score -= i + 1;
//                 if (i + 1 == 27)
//                     score += 30;
//                 if (i + 1 >= 28)
//                     score -= 20;
//             }
//         }
//         return score;
//     }

//     public int getNodesVisited() {
//         return nodesVisited;
//     }
// }
import java.util.List;

public class AI {

    private static final double P_1 = 0.25; // 1/4
    private static final double P_2 = 0.375; // 3/8
    private static final double P_3 = 0.25; // 1/4
    private static final double P_4 = 0.0625; // 1/16
    private static final double P_5 = 0.0625; // 1/16

    private int nodesVisited = 0;

    public Move getBestMove(int[] board, int roll, int depth) {
        nodesVisited = 0;
        Move bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        List<Move> moves = Rules.getPossibleMoves(board, Board.AI, roll);

        if (moves.isEmpty()) {
            return null;
        }

        for (Move move : moves) {
            int[] nextBoard = board.clone();
            Board.applyMove(nextBoard, move, Board.AI);

            double value = expectiminimax(nextBoard, depth - 1, false, 0, Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        return bestMove;
    }

    public double expectiminimax(int[] board, int depth, boolean isMax, int fixedRoll, double alpha, double beta) {
        nodesVisited++;

        if (hasWon(board, Board.AI))
            return 10000 + depth;
        if (hasWon(board, Board.HUMAN))
            return -10000 - depth;

        if (depth == 0) {
            return evaluate(board);
        }

        if (fixedRoll == 0) {
            return P_1 * expectiminimax(board, depth - 1, isMax, 1, alpha, beta) +
                    P_2 * expectiminimax(board, depth - 1, isMax, 2, alpha, beta) +
                    P_3 * expectiminimax(board, depth - 1, isMax, 3, alpha, beta) +
                    P_4 * expectiminimax(board, depth - 1, isMax, 4, alpha, beta) +
                    P_5 * expectiminimax(board, depth - 1, isMax, 5, alpha, beta);
        }

        int player = isMax ? Board.AI : Board.HUMAN;
        List<Move> moves = Rules.getPossibleMoves(board, player, fixedRoll);

        if (moves.isEmpty()) {

            return expectiminimax(board, depth - 1, !isMax, 0, alpha, beta);
        }

        if (isMax) {
            double value = Double.NEGATIVE_INFINITY;
            for (Move m : moves) {
                int[] nextBoard = board.clone();
                Board.applyMove(nextBoard, m, player);
                value = Math.max(value, expectiminimax(nextBoard, depth - 1, false, 0, alpha, beta));
                alpha = Math.max(alpha, value);
                if (beta <= alpha)
                    break;
            }
            return value;
        } else {
            double value = Double.POSITIVE_INFINITY;
            for (Move m : moves) {
                int[] nextBoard = board.clone();
                Board.applyMove(nextBoard, m, player);
                value = Math.min(value, expectiminimax(nextBoard, depth - 1, true, 0, alpha, beta));
                beta = Math.min(beta, value);
                if (beta <= alpha)
                    break;
            }
            return value;
        }
    }

    private double evaluate(int[] board) {
        double score = 0;
        int aiPieces = 0;
        int humanPieces = 0;

        for (int piece : board) {
            if (piece == Board.AI)
                aiPieces++;
            else if (piece == Board.HUMAN)
                humanPieces++;
        }

        score += (5 - humanPieces) * 100;
        score -= (5 - aiPieces) * 100;

        for (int i = 0; i < board.length; i++) {
            if (board[i] == Board.AI) {
                score += (i + 1);
                if (Rules.isProtected(i + 1))
                    score += 10;
                if (i > 0 && board[i - 1] == Board.AI)
                    score += 5;
            } else if (board[i] == Board.HUMAN) {
                score -= (i + 1);
                if (Rules.isProtected(i + 1))
                    score -= 10;
                if (i > 0 && board[i - 1] == Board.HUMAN)
                    score -= 5;
            }
        }
        return score;
    }

    private boolean hasWon(int[] board, int player) {
        for (int b : board) {
            if (b == player)
                return false;
        }
        return true;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }
}