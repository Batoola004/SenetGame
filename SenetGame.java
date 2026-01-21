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

    public SenetGame() {
        initializeBoard();
        initializeUI();
        startGame();
    }

    private void initializeBoard() {
        board = new int[Board.BOARD_SIZE];
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

        // Create panels
        boardPanel = new GameBoardPanel();
        controlPanel = new ControlPanel(this);
        infoPanel = new InfoPanel();

        // Add panels to frame
        mainFrame.add(boardPanel, BorderLayout.CENTER);
        mainFrame.add(controlPanel, BorderLayout.EAST);
        mainFrame.add(infoPanel, BorderLayout.SOUTH);

        // Configure frame
        mainFrame.setSize(1400, 950);
        mainFrame.setMinimumSize(new Dimension(1200, 800));
        mainFrame.setLocationRelativeTo(null);

        // Add window listener for cleanup
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (aiTimer != null && aiTimer.isRunning()) {
                    aiTimer.stop();
                }
            }
        });

        mainFrame.setVisible(true);

        // Initialize AI timer - this timer is just for visual delay, not for execution
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
        infoPanel.showMessage("🎲 Game started! Human player begins.");
        infoPanel.showMessage("🎯 Rules: First to exit all 5 pieces wins!");
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
            infoPanel.showMessage("⚠️ Cannot roll - not human's turn or AI is thinking");
            return;
        }

        int roll = sticks.makeThrow();
        infoPanel.setDiceRoll(roll);
        infoPanel.showMessage("👤 Human rolled: " + roll);

        List<Move> moves = Rules.getPossibleMoves(board, currentPlayer, roll);

        if (moves.isEmpty()) {
            infoPanel.showMessage("❌ No valid moves available. Turn skipped.");
            switchTurn();
        } else {
            controlPanel.showMoveButtons(moves);
            infoPanel.showMessage("✅ Found " + moves.size() + " valid moves for Human.");
        }
    }

    public void executeHumanMove(Move move) {
        if (!gameActive) {
            infoPanel.showMessage("⚠️ Cannot move - game is not active");
            return;
        }

        // Highlight the move on the board
        boardPanel.highlightMove(move);

        // Apply the move
        Board.applyMove(board, move, Board.HUMAN, true);

        // Check if piece exited
        boolean pieceExited = false;
        if (move.to == -1) {
            humanExited++;
            pieceExited = true;
            infoPanel.showMessage("🎉 Human piece exited! (" + humanExited + "/" + TOTAL_PIECES + ")");
        } else {
            int effect = Rules.applySpecialSquareEffect(move.to, board);
            if (effect == -1) {
                humanExited++;
                pieceExited = true;
                infoPanel.showMessage(
                        "🎉 Human piece exited via special square! (" + humanExited + "/" + TOTAL_PIECES + ")");
            }
        }

        updateBoardDisplay();

        // Log the move details
        String moveDetails = String.format("👤 Human moved: %s", move);
        if (pieceExited) {
            moveDetails += " (piece exited)";
        }
        infoPanel.showMessage(moveDetails);

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
        infoPanel.showMessage("🤖 AI is thinking...");

        int roll = sticks.makeThrow();
        infoPanel.setDiceRoll(roll);
        infoPanel.showMessage("🤖 AI rolled: " + roll);

        List<Move> moves = Rules.getPossibleMoves(board, currentPlayer, roll);

        if (moves.isEmpty()) {
            infoPanel.showMessage("🤖 AI has no valid moves. Turn skipped.");
            switchTurn();
            return;
        }

        // Show thinking animation
        boardPanel.highlightMove(null);

        // Get best move from AI
        Move aiMove = ai.getBestMove(board, roll, AI_DEPTH);

        if (aiMove != null) {
            infoPanel.setAIEvaluation(ai.getLastEvaluation(), ai.getNodesVisited());
            infoPanel.showMessage(String.format("🤖 AI chose: %s (Eval: %.2f, Nodes: %d)",
                    aiMove, ai.getLastEvaluation(), ai.getNodesVisited()));

            // Highlight the move
            boardPanel.highlightMove(aiMove);

            // Apply the move
            Board.applyMove(board, aiMove, Board.AI, true);

            // Check if piece exited
            boolean pieceExited = false;
            if (aiMove.to == -1) {
                aiExited++;
                pieceExited = true;
                infoPanel.showMessage("🎉 AI piece exited! (" + aiExited + "/" + TOTAL_PIECES + ")");
            } else {
                int effect = Rules.applySpecialSquareEffect(aiMove.to, board);
                if (effect == -1) {
                    aiExited++;
                    pieceExited = true;
                    infoPanel.showMessage(
                            "🎉 AI piece exited via special square! (" + aiExited + "/" + TOTAL_PIECES + ")");
                }
            }

            updateBoardDisplay();

            // Log the move details
            String moveDetails = String.format("🤖 AI moved: %s", aiMove);
            if (pieceExited) {
                moveDetails += " (piece exited)";
            }
            infoPanel.showMessage(moveDetails);

            checkGameEnd();
        } else {
            infoPanel.showMessage("🤖 AI couldn't find a valid move. Turn skipped.");
        }

        // Reset AI thinking flag BEFORE switching turn
        aiThinking = false;

        if (gameActive) {
            switchTurn();
        }
    }

    private void switchTurn() {
        if (!gameActive) {
            return;
        }

        infoPanel.showMessage("🔄 Switching turn...");

        currentPlayer = -currentPlayer;
        controlPanel.clearMoveButtons();
        updateInfoPanel();

        if (currentPlayer == Board.AI && gameActive) {
            controlPanel.setGameControlsEnabled(false);
            aiThinking = true;
            // Start the timer for visual delay
            aiTimer.start();
        } else if (currentPlayer == Board.HUMAN && gameActive) {
            controlPanel.setGameControlsEnabled(true);
            infoPanel.showMessage("🎯 Human's turn - click 'Roll Sticks'");
        }
    }

    private void checkGameEnd() {
        if (humanExited >= TOTAL_PIECES) {
            infoPanel.showMessage("🏆 HUMAN WINS! All pieces exited!");
            gameActive = false;
            controlPanel.setGameControlsEnabled(false);
            infoPanel.showWinMessage("HUMAN");
        } else if (aiExited >= TOTAL_PIECES) {
            infoPanel.showMessage("🏆 AI WINS! All pieces exited!");
            gameActive = false;
            controlPanel.setGameControlsEnabled(false);
            infoPanel.showWinMessage("AI");
        }
    }

    public void resetGame() {
        // Stop any running timers
        if (aiTimer != null && aiTimer.isRunning()) {
            aiTimer.stop();
        }

        aiThinking = false;
        gameActive = true;

        // Reset game state
        initializeBoard();
        currentPlayer = Board.HUMAN;
        humanExited = 0;
        aiExited = 0;

        // Update UI
        updateBoardDisplay();
        updateInfoPanel();
        controlPanel.clearMoveButtons();
        controlPanel.setGameControlsEnabled(true);
        infoPanel.clearMessages();
        boardPanel.highlightMove(null);

        infoPanel.showMessage("🔄 Game reset. Human player starts again.");
    }

    public static void launchGame() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Error setting look and feel: " + e.getMessage());
                e.printStackTrace();
            }

            new SenetGame();
        });
    }

    // Inner class for ControlPanel to keep it self-contained
    private static class ControlPanel extends JPanel {
        private JButton rollButton;
        private JButton resetButton;
        private JPanel movesPanel;
        private JLabel statusLabel;
        private SenetGame game;

        public ControlPanel(SenetGame game) {
            this.game = game;
            setLayout(new BorderLayout(15, 15));
            setPreferredSize(new Dimension(350, 0));
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
            // Status label with better styling
            statusLabel = new JLabel("Human's Turn", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
            statusLabel.setForeground(new Color(139, 69, 19));
            statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            add(statusLabel, BorderLayout.NORTH);

            // Button panel with better spacing
            JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
            buttonPanel.setBackground(new Color(245, 222, 179));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            rollButton = createStyledButton("Roll Sticks", new Color(34, 139, 34));
            rollButton.addActionListener(e -> game.rollDiceForHuman());
            rollButton.setPreferredSize(new Dimension(200, 60));
            rollButton.setFont(new Font("Arial", Font.BOLD, 16));

            resetButton = createStyledButton("Reset Game", new Color(178, 34, 34));
            resetButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                        game.mainFrame,
                        "Are you sure you want to reset the game?",
                        "Confirm Reset",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    game.resetGame();
                }
            });
            resetButton.setPreferredSize(new Dimension(200, 60));
            resetButton.setFont(new Font("Arial", Font.BOLD, 16));

            buttonPanel.add(rollButton);
            buttonPanel.add(resetButton);
            add(buttonPanel, BorderLayout.CENTER);

            // Moves panel with better scrolling
            movesPanel = new JPanel();
            movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
            movesPanel.setBackground(new Color(245, 222, 179));
            movesPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(184, 134, 11), 2),
                    "Available Moves",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 14),
                    new Color(139, 69, 19)));

            JScrollPane scrollPane = new JScrollPane(movesPanel);
            scrollPane.setPreferredSize(new Dimension(300, 400));
            scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            add(scrollPane, BorderLayout.SOUTH);
        }

        private JButton createStyledButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setBackground(color);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color.darker(), 3),
                    BorderFactory.createEmptyBorder(15, 25, 15, 25)));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Add hover effect
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(color.brighter());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(color);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    button.setBackground(color.darker());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    button.setBackground(color.brighter());
                }
            });

            return button;
        }

        public void showMoveButtons(List<Move> moves) {
            movesPanel.removeAll();

            if (moves.isEmpty()) {
                JLabel noMovesLabel = new JLabel("❌ No moves available", SwingConstants.CENTER);
                noMovesLabel.setForeground(Color.RED);
                noMovesLabel.setFont(new Font("Arial", Font.BOLD, 16));
                movesPanel.add(noMovesLabel);
            } else {
                JLabel titleLabel = new JLabel("🎯 Select a move:", SwingConstants.CENTER);
                titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
                titleLabel.setForeground(new Color(139, 69, 19));
                titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
                movesPanel.add(titleLabel);

                for (int i = 0; i < moves.size(); i++) {
                    Move move = moves.get(i);
                    JButton moveButton = createMoveButton(move, i + 1);
                    movesPanel.add(moveButton);
                    movesPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                }
            }

            movesPanel.revalidate();
            movesPanel.repaint();
        }

        private JButton createMoveButton(Move move, int number) {
            String buttonText = String.format("%d) %s", number, move);
            JButton button = new JButton(buttonText);
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setBackground(new Color(240, 230, 140)); // Khaki
            button.setForeground(new Color(139, 69, 19));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(184, 134, 11), 2),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);

            button.addActionListener(e -> {
                game.executeHumanMove(move);
                clearMoveButtons();
            });

            // Add hover effect to highlight the move on board
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    game.boardPanel.highlightMove(move);
                    button.setBackground(new Color(255, 255, 200));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    game.boardPanel.highlightMove(null);
                    button.setBackground(new Color(240, 230, 140));
                }
            });

            return button;
        }

        public void clearMoveButtons() {
            movesPanel.removeAll();
            JLabel instructionLabel = new JLabel("🎲 Click 'Roll Sticks' to see available moves",
                    SwingConstants.CENTER);
            instructionLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            instructionLabel.setForeground(new Color(139, 69, 19));
            movesPanel.add(instructionLabel);

            movesPanel.revalidate();
            movesPanel.repaint();
            game.boardPanel.highlightMove(null);
        }

        public void setGameControlsEnabled(boolean enabled) {
            rollButton.setEnabled(enabled);
            String statusText = enabled ? "Human's Turn" : "AI's Turn";
            Color statusColor = enabled ? new Color(34, 139, 34) : new Color(178, 34, 34);

            statusLabel.setText(statusText);
            statusLabel.setForeground(statusColor);

            if (enabled) {
                statusLabel.setFont(new Font("Arial", Font.BOLD, 22));
                Timer timer = new Timer(300, e -> {
                    statusLabel.setFont(new Font("Arial", Font.BOLD, 20));
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
}