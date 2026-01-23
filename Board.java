
public class Board {

    public static final int HUMAN = 1;
    public static final int AI = -1;
    public static final int EMPTY = 0;

    public static void applyMove(int[] board, Move move, int player) {
        if (move.to == -1) {
            // Piece exits the board
            board[move.from - 1] = 0;
            return;
        }

        int fromIdx = move.from - 1;
        int toIdx = move.to - 1;
        int opponent = -player;

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