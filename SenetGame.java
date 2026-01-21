
import javax.swing.*;
import java.awt.*;
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
    private final int AI_DEPTH = 3;
    private Throw sticks = new Throw();
    private Timer aiTimer;
    private boolean gameActive = true;

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
        mainFrame.setLayout(new BorderLayout(10, 10));

        Color bgColor = new Color(245, 222, 179);
        mainFrame.getContentPane().setBackground(bgColor);

        boardPanel = new GameBoardPanel();
        controlPanel = new ControlPanel(this);
        infoPanel = new InfoPanel();

        mainFrame.add(boardPanel, BorderLayout.CENTER);
        mainFrame.add(controlPanel, BorderLayout.EAST);
        mainFrame.add(infoPanel, BorderLayout.SOUTH);

        mainFrame.setSize(1200, 800);
        mainFrame.setMinimumSize(new Dimension(1000, 700));
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        aiTimer = new Timer(1500, e -> executeAITurn());
        aiTimer.setRepeats(false);
    }

    private void startGame() {
        updateBoardDisplay();
        updateInfoPanel();
        controlPanel.setGameControlsEnabled(true);
        infoPanel.showMessage("Welcome to Senet! Human starts the game.");
    }

    public void updateBoardDisplay() {
        boardPanel.setBoard(board);
        boardPanel.repaint();
    }

    private void updateInfoPanel() {
        infoPanel.updateInfo(currentPlayer, humanExited, aiExited);
    }

    public void rollDiceForHuman() {
        if (!gameActive || currentPlayer != Board.HUMAN)
            return;

        int roll = sticks.makeThrow();
        infoPanel.setDiceRoll(roll);

        List<Move> moves = Rules.getPossibleMoves(board, currentPlayer, roll);

        if (moves.isEmpty()) {
            infoPanel.showMessage("No valid moves available. Turn skipped.");
            switchTurn();
        } else {
            controlPanel.showMoveButtons(moves);
        }
    }

    public void executeHumanMove(Move move) {
        Board.applyMove(board, move, Board.HUMAN, false);

        if (move.to == -1 || Rules.applySpecialSquareEffect(move.to) == -1) {
            humanExited++;
            infoPanel.showMessage("🎉 Human piece exited! (" + humanExited + "/" + TOTAL_PIECES + ")");
        }

        updateBoardDisplay();
        checkGameEnd();
        switchTurn();
    }

    private void executeAITurn() {
        if (!gameActive || currentPlayer != Board.AI)
            return;

        int roll = sticks.makeThrow();
        infoPanel.setDiceRoll(roll);

        List<Move> moves = Rules.getPossibleMoves(board, currentPlayer, roll);

        if (moves.isEmpty()) {
            infoPanel.showMessage("🤖 AI has no valid moves.");
            switchTurn();
            return;
        }

        Move aiMove = ai.getBestMove(board, roll, AI_DEPTH);

        if (aiMove != null) {
            infoPanel.showMessage("🤖 AI chooses: " + aiMove);
            Board.applyMove(board, aiMove, Board.AI, false);

            if (aiMove.to == -1 || Rules.applySpecialSquareEffect(aiMove.to) == -1) {
                aiExited++;
                infoPanel.showMessage("🎉 AI piece exited! (" + aiExited + "/" + TOTAL_PIECES + ")");
            }

            updateBoardDisplay();
            checkGameEnd();
            switchTurn();
        }
    }

    private void switchTurn() {
        if (!gameActive)
            return;

        currentPlayer = -currentPlayer;
        controlPanel.clearMoveButtons();
        updateInfoPanel();

        if (currentPlayer == Board.AI) {
            controlPanel.setGameControlsEnabled(false);
            aiTimer.start();
        } else {
            controlPanel.setGameControlsEnabled(true);
        }
    }

    private void checkGameEnd() {
        if (humanExited >= TOTAL_PIECES) {
            showGameEndDialog("🏆 HUMAN WINS! All pieces exited!");
            gameActive = false;
            controlPanel.setGameControlsEnabled(false);
        } else if (aiExited >= TOTAL_PIECES) {
            showGameEndDialog("🏆 AI WINS! All pieces exited!");
            gameActive = false;
            controlPanel.setGameControlsEnabled(false);
        }
    }

    private void showGameEndDialog(String message) {
        JOptionPane.showMessageDialog(mainFrame,
                message,
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void resetGame() {
        if (aiTimer != null && aiTimer.isRunning()) {
            aiTimer.stop();
        }

        initializeBoard();
        currentPlayer = Board.HUMAN;
        humanExited = 0;
        aiExited = 0;
        gameActive = true;

        updateBoardDisplay();
        updateInfoPanel();
        controlPanel.clearMoveButtons();
        controlPanel.setGameControlsEnabled(true);
        infoPanel.clearMessages();
        infoPanel.showMessage("Game reset. Human starts the game.");
    }

    public static void launchGame() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new SenetGame();
        });
    }
}