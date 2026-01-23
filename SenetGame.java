import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SenetGame {
    private JFrame mainFrame;
    private GameBoardPanel boardPanel;
    private ControlPanel controlPanel;
    private InfoPanel infoPanel;
    private int[] board;
    private int currentPlayer = Board.HUMAN;
    private int humanExited = 0;
    private int aiExited = 0;
    private final int TOTAL_PIECES = 5;
    private AI ai = new AI();
    private final int AI_DEPTH = 4;
    private Throw sticks = new Throw();
    private Timer aiTimer;
    private boolean gameActive = true;
    private boolean aiThinking = false;

    private static final int BOARD_SIZE = 30;

    public SenetGame() {
        initializeBoard();
        initializeUI();
        startGame();
    }

    private void initializeBoard() {
        board = new int[BOARD_SIZE];
        // Alternating setup
        for (int i = 0; i < 14; i++) {
            board[i] = (i % 2 == 0) ? Board.HUMAN : Board.AI;
        }
    }

    private void initializeUI() {
        mainFrame = new JFrame("Senet - Ancient Egyptian Board Game");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout(15, 15));
        mainFrame.getContentPane().setBackground(new Color(245, 222, 179));

        boardPanel = new GameBoardPanel();
        controlPanel = new ControlPanel(this);
        infoPanel = new InfoPanel();

        mainFrame.add(boardPanel, BorderLayout.CENTER);
        mainFrame.add(controlPanel, BorderLayout.EAST);
        mainFrame.add(infoPanel, BorderLayout.SOUTH);

        mainFrame.setSize(1400, 950);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        aiTimer = new Timer(1500, e -> {
            aiThinking = false;
            if (gameActive && currentPlayer == Board.AI) {
                executeAITurn();
            }
        });
        aiTimer.setRepeats(false);
    }

    private void startGame() {
        updateBoardDisplay();
        updateInfoPanel();
        controlPanel.setGameControlsEnabled(true);
        infoPanel.showMessage("Game started. Human player begins.");
    }

    public void updateBoardDisplay() {
        boardPanel.setBoard(board);
        boardPanel.repaint();
    }

    private void updateInfoPanel() {
        infoPanel.updateInfo(currentPlayer, humanExited, aiExited);
    }

    // --- HUMAN LOGIC ---

    public void rollDiceForHuman() {
        if (!gameActive || currentPlayer != Board.HUMAN || aiThinking) {
            return;
        }

        int roll = sticks.makeThrow();
        infoPanel.setDiceRoll(roll);
        infoPanel.showMessage("Human rolled: " + roll);

        List<Move> moves = Rules.getPossibleMoves(board, currentPlayer, roll);

        if (moves.isEmpty()) {
            infoPanel.showMessage("No valid moves. Turn skipped.");
            // Even if turn is skipped, we must check if pieces on special squares
            // failed to move and need to be reset.
            handleUnplayedSpecialPieces(Board.HUMAN, null);
            updateBoardDisplay();
            switchTurn();
        } else {
            controlPanel.showMoveButtons(moves);
        }
    }

    public void executeHumanMove(Move move) {
        if (!gameActive)
            return;

        performMoveLogic(move, Board.HUMAN);

        // After move is done, check if any OTHER pieces on special squares
        // failed to exit and need to be sent back.
        handleUnplayedSpecialPieces(Board.HUMAN, move);

        updateBoardDisplay();
        checkGameEnd();

        if (gameActive) {
            switchTurn();
        }
    }

    // --- AI LOGIC ---

    private void executeAITurn() {
        if (!gameActive)
            return;

        aiThinking = true;
        infoPanel.showMessage("AI is thinking...");

        int roll = sticks.makeThrow();
        infoPanel.setDiceRoll(roll);
        infoPanel.showMessage("AI rolled: " + roll);

        Move aiMove = ai.getBestMove(board, roll, AI_DEPTH);

        if (aiMove != null) {
            infoPanel.showMessage("AI moves: " + aiMove);
            performMoveLogic(aiMove, Board.AI);

            // Check penalties for AI pieces
            handleUnplayedSpecialPieces(Board.AI, aiMove);
        } else {
            infoPanel.showMessage("AI has no valid moves.");
            // Check penalties even if AI skips turn
            handleUnplayedSpecialPieces(Board.AI, null);
        }

        updateBoardDisplay();
        checkGameEnd();

        aiThinking = false;
        if (gameActive) {
            switchTurn();
        }
    }

    // --- CORE GAMEPLAY HELPERS ---

    /**
     * Applies the move and handles immediate effects (Water -> Rebirth)
     */
    private void performMoveLogic(Move move, int player) {
        boardPanel.highlightMove(move);
        Board.applyMove(board, move, player);

        if (move.to == -1) {
            if (player == Board.HUMAN)
                humanExited++;
            else
                aiExited++;
            infoPanel.showMessage((player == Board.HUMAN ? "Human" : "AI") + " piece exited!");
        } else {
            // Check immediate effects (House of Water)
            int effectSquare = Rules.applySpecialSquareEffect(move.to, board);

            if (effectSquare != move.to) {
                // Relocation triggered (e.g. Water -> Rebirth)
                sendPieceToRebirthOrBefore(move.to, player);
            }
        }
    }

    /**
     * Enforces the rule: "Otherwise, the pawn will be sent back to the House of
     * Rebirth."
     * This runs at the END of a turn.
     * * @param player The current player
     * 
     * @param moveJustMade The move that was just executed (to avoid punishing the
     *                     piece that just arrived)
     */
    private void handleUnplayedSpecialPieces(int player, Move moveJustMade) {
        int[] specialSquares = { Rules.HOUSE_OF_THREE_TRUTHS, Rules.HOUSE_OF_RE_ATUM, Rules.HOUSE_OF_HORUS };

        for (int sq : specialSquares) {
            int idx = sq - 1;

            // If the current player has a piece on a special square...
            if (board[idx] == player) {

                // If this piece JUST arrived here this turn, it is safe.
                if (moveJustMade != null && moveJustMade.to == sq) {
                    continue;
                }

                // If it was already there and didn't exit, apply penalty.
                infoPanel.showMessage("Piece on Square " + sq + " failed to exit!");
                sendPieceToRebirthOrBefore(sq, player);
            }
        }
    }

    /**
     * Moves a piece from currentSquare to House of Rebirth (15).
     * If 15 is occupied, finds the first empty square before 15.
     */
    private void sendPieceToRebirthOrBefore(int currentSquare, int player) {
        int target = Rules.HOUSE_OF_REBIRTH; // 15
        int targetIdx = target - 1;

        // If Rebirth is occupied, search backwards
        while (targetIdx >= 0 && board[targetIdx] != 0) {
            targetIdx--;
        }

        // If valid empty spot found (or board[0] if board is super full)
        if (targetIdx >= 0) {
            board[currentSquare - 1] = 0; // Remove from old spot
            board[targetIdx] = player; // Place in new spot

            String name = (player == Board.HUMAN) ? "Human" : "AI";
            infoPanel.showMessage(name + " piece sent back to Square " + (targetIdx + 1));
        }
    }

    private void switchTurn() {
        currentPlayer = -currentPlayer;
        controlPanel.clearMoveButtons();
        updateInfoPanel();

        if (currentPlayer == Board.AI && gameActive) {
            controlPanel.setGameControlsEnabled(false);
            aiThinking = true;
            aiTimer.start();
        } else if (currentPlayer == Board.HUMAN && gameActive) {
            controlPanel.setGameControlsEnabled(true);
            infoPanel.showMessage("Human turn - click Roll Sticks");
        }
    }

    private void checkGameEnd() {
        if (humanExited >= TOTAL_PIECES) {
            finishGame("HUMAN");
        } else if (aiExited >= TOTAL_PIECES) {
            finishGame("AI");
        }
    }

    private void finishGame(String winner) {
        gameActive = false;
        infoPanel.showMessage(winner + " WINS! All pieces exited!");
        infoPanel.showWinMessage(winner);
        controlPanel.setGameControlsEnabled(false);
    }

    public void resetGame() {
        if (aiTimer != null && aiTimer.isRunning())
            aiTimer.stop();
        gameActive = true;
        aiThinking = false;
        initializeBoard();
        currentPlayer = Board.HUMAN;
        humanExited = 0;
        aiExited = 0;

        updateBoardDisplay();
        updateInfoPanel();
        controlPanel.clearMoveButtons();
        controlPanel.setGameControlsEnabled(true);
        infoPanel.clearMessages();
        infoPanel.showMessage("Game reset. Human starts.");
    }

    public static void launchGame() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
            }
            new SenetGame();
        });
    }

    // --- INNER CLASSES (UI) ---
    // (Keep your existing ControlPanel, InfoPanel, etc. here)
    private static class ControlPanel extends JPanel {
        // ... (Keep existing ControlPanel code exactly as provided in previous prompt)
        // ...
        // For brevity, I am assuming the previous ControlPanel code is pasted here
        // as it does not require logical changes, only the SenetGame reference.
        private JButton rollButton;
        private JButton resetButton;
        private JPanel movesPanel;
        private JLabel statusLabel;
        private SenetGame game;

        public ControlPanel(SenetGame game) {
            this.game = game;
            setLayout(new BorderLayout(15, 15));
            setBackground(new Color(245, 222, 179));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            initializeComponents();
        }

        private void initializeComponents() {
            statusLabel = new JLabel("Human Turn", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
            add(statusLabel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
            buttonPanel.setBackground(new Color(245, 222, 179));

            rollButton = new JButton("Roll Sticks");
            rollButton.addActionListener(e -> game.rollDiceForHuman());

            resetButton = new JButton("Reset Game");
            resetButton.addActionListener(e -> game.resetGame());

            buttonPanel.add(rollButton);
            buttonPanel.add(resetButton);
            add(buttonPanel, BorderLayout.CENTER);

            movesPanel = new JPanel();
            movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
            add(new JScrollPane(movesPanel), BorderLayout.SOUTH);
        }

        public void showMoveButtons(List<Move> moves) {
            movesPanel.removeAll();
            for (Move m : moves) {
                JButton btn = new JButton(m.toString());
                btn.addActionListener(e -> {
                    game.executeHumanMove(m);
                    clearMoveButtons();
                });
                movesPanel.add(btn);
            }
            movesPanel.revalidate();
            movesPanel.repaint();
        }

        public void clearMoveButtons() {
            movesPanel.removeAll();
            movesPanel.revalidate();
            movesPanel.repaint();
        }

        public void setGameControlsEnabled(boolean enabled) {
            rollButton.setEnabled(enabled);
            statusLabel.setText(enabled ? "Human Turn" : "AI Turn");
        }
    }
}