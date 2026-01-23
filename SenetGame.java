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
        for (int i = 0; i < 14; i++) {
            board[i] = (i % 2 == 0) ? Board.HUMAN : Board.AI;
        }
    }

    private void initializeUI() {
        mainFrame = new JFrame("Senet - Ancient Egyptian Board Game");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout(15, 15));

        Color bgColor = new Color(245, 222, 179);
        mainFrame.getContentPane().setBackground(bgColor);

        boardPanel = new GameBoardPanel();
        controlPanel = new ControlPanel(this);
        infoPanel = new InfoPanel();

        mainFrame.add(boardPanel, BorderLayout.CENTER);
        mainFrame.add(controlPanel, BorderLayout.EAST);
        mainFrame.add(infoPanel, BorderLayout.SOUTH);

        mainFrame.setSize(1400, 950);
        mainFrame.setMinimumSize(new Dimension(1200, 800));
        mainFrame.setLocationRelativeTo(null);

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (aiTimer != null && aiTimer.isRunning()) {
                    aiTimer.stop();
                }
            }
        });

        mainFrame.setVisible(true);

        aiTimer = new Timer(1000, e -> {
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
        infoPanel.clearMessages();
        infoPanel.showMessage("Game started. Human player begins.");
        infoPanel.showMessage("Rules: First to exit all 5 pieces wins.");
    }

    public void updateBoardDisplay() {
        boardPanel.setBoard(board);
        boardPanel.repaint();
    }

    private void updateInfoPanel() {
        infoPanel.updateInfo(currentPlayer, humanExited, aiExited);
    }

    public void rollDiceForHuman() {
        if (!gameActive || currentPlayer != Board.HUMAN || aiThinking) {
            infoPanel.showMessage("Warning: Cannot roll - not human turn or AI is thinking");
            return;
        }

        int roll = sticks.makeThrow();
        infoPanel.setDiceRoll(roll);
        infoPanel.showMessage("Human rolled: " + roll);

        List<Move> moves = Rules.getPossibleMoves(board, currentPlayer, roll);

        if (moves.isEmpty()) {
            infoPanel.showMessage("No valid moves. Turn skipped.");
            // CRITICAL: Even if turn is skipped, check if pieces on 28/29/30 failed to exit
            handleUnplayedSpecialPieces(Board.HUMAN, null);
            updateBoardDisplay();
            switchTurn();
        } else {
            controlPanel.showMoveButtons(moves);
            infoPanel.showMessage("Found " + moves.size() + " valid moves.");
        }
    }

    public void executeHumanMove(Move move) {
        if (!gameActive) {
            infoPanel.showMessage("Warning: Cannot move - game is inactive");
            return;
        }

        performMoveLogic(move, Board.HUMAN);

        // Logic Check: Any piece on 28/29/30 that wasn't just moved is now drowned
        handleUnplayedSpecialPieces(Board.HUMAN, move);

        updateBoardDisplay();
        checkGameEnd();

        if (gameActive) {
            switchTurn();
        }
    }

    private void executeAITurn() {
        if (!gameActive || currentPlayer != Board.AI || aiThinking) {
            return;
        }

        aiThinking = true;
        infoPanel.showMessage("AI is thinking...");

        int roll = sticks.makeThrow();
        infoPanel.setDiceRoll(roll);
        infoPanel.showMessage("AI rolled: " + roll);

        Move aiMove = ai.getBestMove(board, roll, AI_DEPTH);

        if (aiMove != null) {
            infoPanel.showMessage("AI chose: " + aiMove);
            performMoveLogic(aiMove, Board.AI);
            handleUnplayedSpecialPieces(Board.AI, aiMove);
        } else {
            infoPanel.showMessage("AI has no valid moves. Turn skipped.");
            handleUnplayedSpecialPieces(Board.AI, null);
        }

        updateBoardDisplay();
        checkGameEnd();

        aiThinking = false;
        if (gameActive) {
            switchTurn();
        }
    }

    // --- NEW LOGIC HELPERS ---

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
            // Check Immediate Effect (House of Water -> Rebirth)
            int effect = Rules.applySpecialSquareEffect(move.to, board);
            if (effect != move.to) {
                // If Rules say goto 15, but 15 is full, find spot before
                sendPieceToRebirthOrBefore(move.to, player);
            }
        }
    }

    private void handleUnplayedSpecialPieces(int player, Move moveJustMade) {
        int[] specialSquares = { Rules.HOUSE_OF_THREE_TRUTHS, Rules.HOUSE_OF_RE_ATUM, Rules.HOUSE_OF_HORUS };

        for (int sq : specialSquares) {
            int idx = sq - 1;
            // If piece belongs to current player
            if (board[idx] == player) {
                // If it is NOT the piece we just moved (meaning it sat there this turn)
                if (moveJustMade != null && moveJustMade.to == sq) {
                    continue; // Safe, just arrived
                }

                // Then it failed to exit -> Send to Rebirth
                infoPanel.showMessage("Piece on Square " + sq + " failed to exit and drowns!");
                sendPieceToRebirthOrBefore(sq, player);
            }
        }
    }

    private void sendPieceToRebirthOrBefore(int currentSquare, int player) {
        // Start looking at Rebirth (15)
        int targetIdx = Rules.HOUSE_OF_REBIRTH - 1;

        // If occupied, search backwards
        while (targetIdx >= 0 && board[targetIdx] != 0) {
            targetIdx--;
        }

        if (targetIdx >= 0) {
            board[currentSquare - 1] = 0; // Empty old spot
            board[targetIdx] = player; // Fill new spot
            String pName = (player == Board.HUMAN) ? "Human" : "AI";
            infoPanel.showMessage(pName + " piece relocated to Square " + (targetIdx + 1));
        }
    }

    // --- END NEW LOGIC HELPERS ---

    private void switchTurn() {
        if (!gameActive)
            return;

        infoPanel.showMessage("Switching turn...");
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
            infoPanel.showMessage("HUMAN WINS! All pieces exited!");
            gameActive = false;
            controlPanel.setGameControlsEnabled(false);
            infoPanel.showWinMessage("HUMAN");
        } else if (aiExited >= TOTAL_PIECES) {
            infoPanel.showMessage("AI WINS! All pieces exited!");
            gameActive = false;
            controlPanel.setGameControlsEnabled(false);
            infoPanel.showWinMessage("AI");
        }
    }

    public void resetGame() {
        if (aiTimer != null && aiTimer.isRunning()) {
            aiTimer.stop();
        }

        aiThinking = false;
        gameActive = true;
        initializeBoard();
        currentPlayer = Board.HUMAN;
        humanExited = 0;
        aiExited = 0;

        updateBoardDisplay();
        updateInfoPanel();
        controlPanel.clearMoveButtons();
        controlPanel.setGameControlsEnabled(true);
        infoPanel.clearMessages();
        boardPanel.highlightMove(null);

        infoPanel.showMessage("Game reset. Human player starts.");
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

    // --- YOUR ORIGINAL CONTROL PANEL (PRESERVED) ---
    private static class ControlPanel extends JPanel {
        private JButton rollButton;
        private JButton resetButton;
        private JPanel movesPanel;
        private JLabel statusLabel;
        private SenetGame game;

        public ControlPanel(SenetGame game) {
            this.game = game;
            setLayout(new BorderLayout(15, 15));
            setBackground(new Color(245, 222, 179));
            setBorder(createBorder());
            initializeComponents();
        }

        private Border createBorder() {
            return BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(
                            BorderFactory.createLineBorder(new Color(139, 69, 19), 3),
                            "Game Controls",
                            TitledBorder.CENTER,
                            TitledBorder.TOP,
                            new Font("Arial", Font.BOLD, 18),
                            new Color(139, 69, 19)),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15));
        }

        private void initializeComponents() {
            statusLabel = new JLabel("Human Turn", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
            statusLabel.setForeground(new Color(139, 69, 19));
            statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            add(statusLabel, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
            buttonPanel.setBackground(new Color(245, 222, 179));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            rollButton = createStyledButton("Roll Sticks", new Color(34, 139, 34));
            rollButton.addActionListener(e -> game.rollDiceForHuman());

            resetButton = createStyledButton("Reset Game", new Color(178, 34, 34));
            resetButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(game.mainFrame, "Reset game?");
                if (confirm == JOptionPane.YES_OPTION) {
                    game.resetGame();
                }
            });

            buttonPanel.add(rollButton);
            buttonPanel.add(resetButton);
            add(buttonPanel, BorderLayout.CENTER);

            movesPanel = new JPanel();
            movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
            movesPanel.setBackground(new Color(245, 222, 179));
            movesPanel.setBorder(BorderFactory.createTitledBorder("Available Moves"));

            JScrollPane scrollPane = new JScrollPane(movesPanel);
            scrollPane.setPreferredSize(new Dimension(300, 400));
            add(scrollPane, BorderLayout.SOUTH);
        }

        private JButton createStyledButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return button;
        }

        public void showMoveButtons(List<Move> moves) {
            movesPanel.removeAll();
            if (moves.isEmpty()) {
                movesPanel.add(new JLabel("No moves available"));
            } else {
                for (int i = 0; i < moves.size(); i++) {
                    Move move = moves.get(i);
                    JButton mBtn = new JButton((i + 1) + ") " + move.toString());
                    mBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
                    mBtn.addActionListener(e -> {
                        game.executeHumanMove(move);
                        clearMoveButtons();
                    });

                    mBtn.addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) {
                            game.boardPanel.highlightMove(move);
                        }

                        public void mouseExited(MouseEvent e) {
                            game.boardPanel.highlightMove(null);
                        }
                    });

                    movesPanel.add(mBtn);
                    movesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
            movesPanel.revalidate();
            movesPanel.repaint();
        }

        public void clearMoveButtons() {
            movesPanel.removeAll();
            movesPanel.add(new JLabel("Roll to see moves"));
            movesPanel.revalidate();
            movesPanel.repaint();
            game.boardPanel.highlightMove(null);
        }

        public void setGameControlsEnabled(boolean enabled) {
            rollButton.setEnabled(enabled);
            statusLabel.setText(enabled ? "Human Turn" : "AI Turn");
            statusLabel.setForeground(enabled ? new Color(34, 139, 34) : new Color(178, 34, 34));
        }
    }
}