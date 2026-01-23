// import java.util.*;

// public class Board {
//     public static final int BOARD_SIZE = 30;
//     public static final int HUMAN = 1;
//     public static final int AI = -1;
//     public static final int EMPTY = 0;

//     public static void printBoard(int[] board) {
//         System.out.println("--- SENET BOARD STATE ---");
//         for (int i = 0; i < 30; i++) {
//             String p = (board[i] == HUMAN) ? "[H]" : (board[i] == AI) ? "[A]" : "[.]";
//             System.out.print(p + " ");
//             if ((i + 1) % 10 == 0)
//                 System.out.println();
//         }
//         System.out.println("-------------------------");
//     }

//     public static void applyMove(int[] board, Move move, int player, boolean verbose) {
//         // System.out.println("DEBUG BOARD: applying move from " + move.from + " to " +
//         // move.to);
//         int from = move.from - 1;
//         int to = move.to - 1;

//         if (move.from == 0) {
//             if (board[0] == EMPTY) {
//                 board[0] = player;
//                 return;
//             }
//         }

//         if (from >= 0 && from < BOARD_SIZE && board[from] == player) {
//             if (to >= 0 && to < BOARD_SIZE && board[to] != EMPTY) {
//                 if (!Rules.isProtected(to + 1)) {
//                     int temp = board[to];
//                     board[to] = player;
//                     board[from] = temp;
//                 }
//             } else {
//                 board[from] = EMPTY;
//                 if (to >= 0 && to < BOARD_SIZE)
//                     board[to] = player;
//             }
//         }

//         if (to >= 0 && to < BOARD_SIZE) {
//             int result = Rules.applySpecialSquareEffect(to + 1, board);
//             if (result == -1) {
//                 board[to] = EMPTY;
//             } else if (result != to + 1) {
//                 board[to] = EMPTY;
//                 int newIdx = result - 1;
//                 if (board[newIdx] != EMPTY) {
//                     int search = newIdx - 1;
//                     while (search >= 0 && board[search] != EMPTY) {
//                         search--;
//                     }
//                     if (search >= 0)
//                         board[search] = player;
//                     else
//                         board[0] = player;
//                 } else {
//                     board[newIdx] = player;
//                 }
//             }
//         }
//     }
// }

// public class Board {

//     public static final int HUMAN = 1;
//     public static final int AI = -1;
//     public static final int EMPTY = 0;

//     public static void applyMove(int[] board, Move move, int player) {
//         int from = move.from - 1;
//         int to = move.to - 1;

//         if (from >= 0)
//             board[from] = EMPTY;

//         if (to == -1)
//             return;

//         if (board[to] != EMPTY && !Rules.isProtected(to + 1)) {
//             int temp = board[to];
//             board[to] = player;
//             board[from] = temp;
//         } else {
//             board[to] = player;
//         }

//         int result = Rules.applySpecialSquareEffect(to + 1, board);
//         if (result == -1)
//             board[to] = EMPTY;
//         else if (result != to + 1) {
//             board[to] = EMPTY;
//             board[result - 1] = player;
//         }
//     }
// }
public class Board {

    public static final int HUMAN = 1;
    public static final int AI = -1;
    public static final int EMPTY = 0;

    /**
     * Applies a move to the board.
     * move.from: 1-30 (square number)
     * move.to: 1-30 (square number) OR -1 (exiting the board)
     */
    public static void applyMove(int[] board, Move move, int player) {
        if (move.to == -1) {
            // Piece exits the board
            board[move.from - 1] = 0;
            return;
        }

        int fromIdx = move.from - 1;
        int toIdx = move.to - 1;
        int opponent = -player;

        // Check if landing on opponent's piece (swap positions)
        if (board[toIdx] == opponent && !Rules.isProtected(move.to)) {
            // Swap positions
            board[toIdx] = player;
            board[fromIdx] = opponent;
        } else {
            // Normal move
            board[toIdx] = player;
            board[fromIdx] = 0;
        }
    }
}