
public class Board {

    // أعداد ثابتة لتحديد ماذا يوجد على هذا المربع
    public static final int HUMAN = 1;
    public static final int AI = -1;
    public static final int EMPTY = 0;

    public static void applyMove(int[] board, Move move, int player) {
        if (move.to == -1) {
            // حالة الخروج من اللوحة
            board[move.from - 1] = 0;
            return;
        }

        int fromIdx = move.from - 1;
        int toIdx = move.to - 1;
        int opponent = -player;

        // الحركة اوصلتنا الى قطعة اللاعب الاخر فهنا سيحدث تبادل بين القطعتين
        if (board[toIdx] == opponent && !Rules.isProtected(move.to)) {
            board[toIdx] = player;
            board[fromIdx] = opponent;
        } else {
            // نقل القطع لمربع فارغ
            // وضع القطعة في المربع الجديد
            board[toIdx] = player;
            // إفراغ المربع القديم
            board[fromIdx] = 0;
        }
    }
}