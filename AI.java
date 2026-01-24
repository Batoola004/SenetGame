import java.util.List;

public class AI {

    // احتمالات ظهور الارقام عند رمي العصي
    private static final double P_1 = 0.25; // 1/4
    private static final double P_2 = 0.375; // 3/8
    private static final double P_3 = 0.25; // 1/4
    private static final double P_4 = 0.0625; // 1/16
    private static final double P_5 = 0.0625; // 1/16

    private int nodesVisited = 0;

    public Move getBestMove(int[] board, int roll, int depth) {
        nodesVisited = 0;
        Move bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;// أفضل قيمة وهي أقل قيمة

        // جميع الحركات الممكنة بناء على الرقم الذي ظهر في رمي العصي
        List<Move> moves = Rules.getPossibleMoves(board, Board.AI, roll);

        // اذا لم يوجد حركات ممكنة يرجع null
        if (moves.isEmpty()) {
            return null;
        }

        for (Move move : moves) {
            // ننسخ اللوحة الحالية بعد تطبيق الحركة لتجنب تعديل اللوحة الاصلية
            int[] nextBoard = board.clone();
            // تطبيق الحركة على البورد
            Board.applyMove(nextBoard, move, Board.AI);

            // تطبيق الخوارزمية
            double value = expectiminimax(nextBoard, depth - 1, false, 0, Double.NEGATIVE_INFINITY,
                    Double.POSITIVE_INFINITY);

            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        // افضل حركة للكمبيوتر
        return bestMove;
    }

    // الخوارزمية
    public double expectiminimax(int[] board, int depth, boolean isMax, int fixedRoll, double alpha, double beta) {
        nodesVisited++;

        // شروط التوقف
        // إذا فاز الحاسوب : قيمة موجبة كبيرة + العمق (لتشجيع الفوز السريع)
        if (hasWon(board, Board.AI))
            return 10000 + depth;
        // إذا فاز الإنسان: قيمة سالبة كبيرة - العمق (لعقاب الخسارة السريعة)
        if (hasWon(board, Board.HUMAN))
            return -10000 - depth;

        // إذا وصلنا لأقصى عمق: نرجع تقييم اللوحة الحالية
        if (depth == 0) {
            return evaluate(board);
        }

        // نحن لانعرف نتيجة الرمية التالية لذلك نحسب فرصة ظهور كل احتمال وذلك بالقانون
        // :مجموع احتمالات النتائج *قيمها
        if (fixedRoll == 0) {
            return P_1 * expectiminimax(board, depth - 1, isMax, 1, alpha, beta) +
                    P_2 * expectiminimax(board, depth - 1, isMax, 2, alpha, beta) +
                    P_3 * expectiminimax(board, depth - 1, isMax, 3, alpha, beta) +
                    P_4 * expectiminimax(board, depth - 1, isMax, 4, alpha, beta) +
                    P_5 * expectiminimax(board, depth - 1, isMax, 5, alpha, beta);
        }

        // لنحدد اللاعب الحالي اذا كان ماكس فالحاسوب هو الذي يلعب والا يكون الانسان
        int player = isMax ? Board.AI : Board.HUMAN;

        // الحصول على الحركات الممكنة للاعب الحالي مع الرمية المحددة
        List<Move> moves = Rules.getPossibleMoves(board, player, fixedRoll);

        // إذا لم توجد حركات نتخطى الدور وننتقل للاعب الآخر
        if (moves.isEmpty()) {

            // الانتقال للدور التالي مع الرمية غير المحددة (0)
            return expectiminimax(board, depth - 1, !isMax, 0, alpha, beta);
        }

        // اذا كان دور الحاسوب نبدأ من اقل قيمة
        if (isMax) {
            double value = Double.NEGATIVE_INFINITY;
            for (Move m : moves) {
                int[] nextBoard = board.clone();
                Board.applyMove(nextBoard, m, player);
                // البحث عن القيمة القصوى
                value = Math.max(value, expectiminimax(nextBoard, depth - 1, false, 0, alpha, beta));
                // تحديث ألفا
                alpha = Math.max(alpha, value);
                if (beta <= alpha)
                    break;
            }
            return value;
        } else
        // عقدة اللاعب الانسان نبدأفيه من اعلى قيمة
        {
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

    // تقييم اللوحة الحالية واعطائها قيمة
    // الدرجة الايجابية جيدة للحاسوب
    // الدرجة السلبية جيدة للانسان
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

        // حساب نقاط بناء على عدد القطع التي خرجت
        score += (7 - humanPieces) * 100;
        score -= (7 - aiPieces) * 100;

        // هن حساب النقاط بناء على مواقع القطع
        for (int i = 0; i < board.length; i++) {
            if (board[i] == Board.AI) {
                score += (i + 1);
                if (Rules.isProtected(i + 1))
                    score += 10;
                if (i > 0 && board[i - 1] == Board.AI)
                    score += 7;
            } else if (board[i] == Board.HUMAN) {
                score -= (i + 1);
                if (Rules.isProtected(i + 1))
                    score -= 10;
                if (i > 0 && board[i - 1] == Board.HUMAN)
                    score -= 7;
            }
        }
        return score;
    }

    // اذا لم تبقى قطع لاعب ما في اللوحة فهو يفوز
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