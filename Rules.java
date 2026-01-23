import java.util.*;

public class Rules {

    // ارقام المربعات المميزة
    public static final int HOUSE_OF_REBIRTH = 15;
    public static final int HOUSE_OF_HAPPINESS = 26;
    public static final int HOUSE_OF_WATER = 27;
    public static final int HOUSE_OF_THREE_TRUTHS = 28;
    public static final int HOUSE_OF_RE_ATUM = 29;
    public static final int HOUSE_OF_HORUS = 30;

    // تابع الحركات الممكنة حيث يأخذ اللوحة الحالية وماهو اللاعب وماهي نتيجة الرمي
    public static List<Move> getPossibleMoves(int[] board, int player, int roll) {
        List<Move> moves = new ArrayList<>();

        // فحص مربعات اللوحة
        for (int i = 0; i < board.length; i++) {
            // اذا كان المربع ليس فيه قطعة للاعب الذي دوره الان نتخطاه
            if (board[i] != player)
                continue;

            int from = i + 1; // وضع المؤشر على رقم المربع
            int to = from + roll; // حساب المربع الهدف

            // لا يمكن القفز فوق هذا المربع يجب الوقوف عليه بالضبط
            if (from < HOUSE_OF_HAPPINESS && to > HOUSE_OF_HAPPINESS) {
                continue;
            }

            // في هذا المربع لا نستطيع الخروج منه الا اذا حصلنا على 3 في الرمية
            if (from == HOUSE_OF_THREE_TRUTHS) {
                if (roll == 3) {
                    moves.add(new Move(from, -1));
                }
                continue;
            }

            // وهنا نخرج منه بالحصول على رميتين
            if (from == HOUSE_OF_RE_ATUM) {
                if (roll == 2) {
                    moves.add(new Move(from, -1));
                }
                continue;
            }

            // نخرج بأي رمية نحصل عليها
            if (from == HOUSE_OF_HORUS) {
                moves.add(new Move(from, -1));
                continue;
            }

            // فحص الحركة العادية
            if (to > BOARD_SIZE) {
                // يمكن الخروج اذا كانت القطعة بعد المربع 30
                if (from > HOUSE_OF_HAPPINESS) {
                    moves.add(new Move(from, -1));
                }
                continue;
            }

            // التحقق من صحة الحركة العادية
            if (isValid(board, player, to)) {
                moves.add(new Move(from, to));
            }
        }

        return moves;
    }

    // حجم اللوحة
    private static final int BOARD_SIZE = 30;

    // تابع للتحقق مما اذا كانت الحركة ممكنة
    private static boolean isValid(int[] board, int player, int sq) {
        int o = board[sq - 1];
        return o == 0 || (o != player && !isProtected(sq));
    }

    // تابع للتحقق من المربعات المحمية المميزة
    public static boolean isProtected(int sq) {
        // 15, 26, 28, 29, 30 are protected
        return sq == HOUSE_OF_REBIRTH ||
                sq == HOUSE_OF_HAPPINESS ||
                sq == HOUSE_OF_THREE_TRUTHS ||
                sq == HOUSE_OF_RE_ATUM ||
                sq == HOUSE_OF_HORUS;
    }

    // تابع خاص بتأثير الهبوط على المربع 27 الذي ينقلنا الى المربع 15 مباشرة
    public static int applySpecialSquareEffect(int sq, int[] board) {
        if (sq == HOUSE_OF_WATER) {
            return HOUSE_OF_REBIRTH; // Send to 15
        }
        return sq;
    }
}