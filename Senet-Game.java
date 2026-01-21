// package logic;

// // SenetGame.java - Main GUI class
// import javax.swing.*;
// import javax.swing.border.*;

// import AI;
// import Board;
// import Move;
// import Rules;

// import java.awt.*;
// import java.awt.event.*;
// import java.util.List;

// public class SenetGame {
// private JFrame mainFrame;
// private GameBoardPanel boardPanel;
// private ControlPanel controlPanel;
// private InfoPanel infoPanel;
// private int[] board;
// private int currentPlayer = Board.HUMAN;
// private int humanExited = 0;
// private int aiExited = 0;
// private final int TOTAL_PIECES = 5;
// private AI ai = new AI();
// private final int AI_DEPTH = 3;
// private Throw sticks = new Throw();
// private Timer aiTimer;

// public SenetGame() {
// initializeBoard();
// initializeUI();
// startGame();
// }

// private void initializeBoard() {
// board = new int[Board.BOARD_SIZE];
// for (int i = 0; i < 14; i++) {
// board[i] = (i % 2 == 0) ? Board.HUMAN : Board.AI;
// }
// }

// private void initializeUI() {
// mainFrame = new JFrame("Senet - Ancient Egyptian Board Game");
// mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// mainFrame.setLayout(new BorderLayout(10, 10));

// // Set Egyptian-themed colors
// Color bgColor = new Color(245, 222, 179); // Wheat color
// Color darkBg = new Color(139, 69, 19); // Saddle Brown
// Color goldColor = new Color(212, 175, 55); // Gold

// mainFrame.getContentPane().setBackground(bgColor);

// // Create panels
// boardPanel = new GameBoardPanel();
// controlPanel = new ControlPanel();
// infoPanel = new InfoPanel();

// // Add panels to frame
// mainFrame.add(boardPanel, BorderLayout.CENTER);
// mainFrame.add(controlPanel, BorderLayout.EAST);
// mainFrame.add(infoPanel, BorderLayout.SOUTH);

// // Configure frame
// mainFrame.setSize(1200, 800);
// mainFrame.setMinimumSize(new Dimension(1000, 700));
// mainFrame.setLocationRelativeTo(null);
// mainFrame.setVisible(true);

// // Initialize AI timer
// aiTimer = new Timer(1500, e -> executeAITurn());
// aiTimer.setRepeats(false);
// }

// private void startGame() {
// updateBoardDisplay();
// updateInfoPanel();
// controlPanel.setGameControlsEnabled(true);
// }

// private void updateBoardDisplay() {
// boardPanel.setBoard(board);
// boardPanel.repaint();
// }

// private void updateInfoPanel() {
// infoPanel.updateInfo(currentPlayer, humanExited, aiExited);
// }

// private void rollDiceForHuman() {
// if (currentPlayer != Board.HUMAN)
// return;

// int roll = sticks.makeThrow();
// infoPanel.setDiceRoll(roll);

// List<Move> moves = Rules.getPossibleMoves(board, currentPlayer, roll);

// if (moves.isEmpty()) {
// JOptionPane.showMessageDialog(mainFrame,
// "No valid moves available. Turn skipped.",
// "No Moves",
// JOptionPane.INFORMATION_MESSAGE);
// switchTurn();
// } else {
// controlPanel.showMoveButtons(moves);
// }
// }

// private void executeHumanMove(Move move) {
// Board.applyMove(board, move, Board.HUMAN, true);

// if (move.to == -1 || Rules.applySpecialSquareEffect(move.to) == -1) {
// humanExited++;
// infoPanel.showMessage("🎉 Human piece exited! (" + humanExited + "/" +
// TOTAL_PIECES + ")");
// }

// updateBoardDisplay();
// checkGameEnd();
// switchTurn();
// }

// private void executeAITurn() {
// if (currentPlayer != Board.AI)
// return;

// int roll = sticks.makeThrow();
// infoPanel.setDiceRoll(roll);

// List<Move> moves = Rules.getPossibleMoves(board, currentPlayer, roll);

// if (moves.isEmpty()) {
// infoPanel.showMessage("🤖 AI has no valid moves.");
// switchTurn();
// return;
// }

// Move aiMove = ai.getBestMove(board, roll, AI_DEPTH);

// if (aiMove != null) {
// infoPanel.showMessage("🤖 AI chooses: " + aiMove);
// Board.applyMove(board, aiMove, Board.AI, true);

// if (aiMove.to == -1 || Rules.applySpecialSquareEffect(aiMove.to) == -1) {
// aiExited++;
// infoPanel.showMessage("🎉 AI piece exited! (" + aiExited + "/" + TOTAL_PIECES
// + ")");
// }

// updateBoardDisplay();
// checkGameEnd();
// switchTurn();
// }
// }

// private void switchTurn() {
// currentPlayer = -currentPlayer;
// controlPanel.clearMoveButtons();
// updateInfoPanel();

// if (currentPlayer == Board.AI) {
// controlPanel.setGameControlsEnabled(false);
// aiTimer.start();
// } else {
// controlPanel.setGameControlsEnabled(true);
// }
// }

// private void checkGameEnd() {
// if (humanExited >= TOTAL_PIECES) {
// showGameEndDialog("🏆 HUMAN WINS! All pieces exited!");
// controlPanel.setGameControlsEnabled(false);
// } else if (aiExited >= TOTAL_PIECES) {
// showGameEndDialog("🏆 AI WINS! All pieces exited!");
// controlPanel.setGameControlsEnabled(false);
// }
// }

// private void showGameEndDialog(String message) {
// JOptionPane.showMessageDialog(mainFrame,
// message,
// "Game Over",
// JOptionPane.INFORMATION_MESSAGE);
// }

// // Inner class for the game board panel
// class GameBoardPanel extends JPanel {
// private int[] currentBoard;
// private Color boardColor = new Color(205, 133, 63); // Peru
// private Color safeColor = new Color(154, 205, 50); // Yellow Green
// private Color specialColor = new Color(218, 165, 32); // Goldenrod
// private Color highlightColor = new Color(255, 215, 0, 100); // Gold with
// transparency
// private Move highlightedMove = null;

// public GameBoardPanel() {
// setPreferredSize(new Dimension(800, 600));
// setBackground(new Color(245, 222, 179)); // Wheat
// setBorder(BorderFactory.createCompoundBorder(
// BorderFactory.createLineBorder(new Color(139, 69, 19), 4),
// BorderFactory.createEmptyBorder(20, 20, 20, 20)));
// }

// public void setBoard(int[] board) {
// this.currentBoard = board.clone();
// repaint();
// }

// public void highlightMove(Move move) {
// this.highlightedMove = move;
// repaint();
// }

// @Override
// protected void paintComponent(Graphics g) {
// super.paintComponent(g);
// Graphics2D g2d = (Graphics2D) g;
// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
// RenderingHints.VALUE_ANTIALIAS_ON);

// if (currentBoard == null)
// return;

// // Draw the board in 3 rows
// drawBoardRow(g2d, 0, 9, 50, 100, true); // Row 1: 1-10
// drawBoardRow(g2d, 19, 10, 50, 250, false); // Row 2: 20-11
// drawBoardRow(g2d, 20, 29, 50, 400, true); // Row 3: 21-30

// // Draw title
// g2d.setFont(new Font("Arial", Font.BOLD, 24));
// g2d.setColor(new Color(139, 69, 19));
// String title = "SENET - Ancient Egyptian Game";
// FontMetrics fm = g2d.getFontMetrics();
// int titleWidth = fm.stringWidth(title);
// g2d.drawString(title, (getWidth() - titleWidth) / 2, 40);

// // Draw legend
// drawLegend(g2d, 50, 500);
// }

// private void drawBoardRow(Graphics2D g2d, int start, int end, int x, int y,
// boolean leftToRight) {
// int squareSize = 60;
// int spacing = 10;

// int direction = leftToRight ? 1 : -1;
// int xPos = x;

// for (int i = start; leftToRight ? i <= end : i >= end; i += direction) {
// // Draw square
// Color squareColor = getSquareColor(i);
// g2d.setColor(squareColor);
// g2d.fillRoundRect(xPos, y, squareSize, squareSize, 10, 10);
// g2d.setColor(Color.BLACK);
// g2d.drawRoundRect(xPos, y, squareSize, squareSize, 10, 10);

// // Highlight if this is part of a move
// if (highlightedMove != null) {
// if (highlightedMove.from == i + 1 || highlightedMove.to == i + 1) {
// g2d.setColor(highlightColor);
// g2d.fillRoundRect(xPos, y, squareSize, squareSize, 10, 10);
// }
// }

// // Draw square number
// g2d.setColor(Color.BLACK);
// g2d.setFont(new Font("Arial", Font.BOLD, 12));
// g2d.drawString(String.valueOf(i + 1), xPos + 5, y + 15);

// // Draw piece if present
// if (currentBoard[i] != Board.EMPTY) {
// drawPiece(g2d, xPos, y, squareSize, currentBoard[i]);
// }

// // Draw special square symbols
// drawSpecialSymbol(g2d, xPos, y, squareSize, i + 1);

// xPos += squareSize + spacing;
// }
// }

// private Color getSquareColor(int index) {
// int square = index + 1;
// if (square == Rules.HOUSE_OF_HAPPINESS ||
// square == Rules.HOUSE_OF_THREE_TRUTHS ||
// square == Rules.HOUSE_OF_RE_ATUM ||
// square == Rules.HOUSE_OF_HORUS) {
// return specialColor;
// } else if (square == Rules.HOUSE_OF_REBIRTH || square ==
// Rules.HOUSE_OF_WATER) {
// return safeColor;
// } else {
// return boardColor;
// }
// }

// private void drawPiece(Graphics2D g2d, int x, int y, int size, int player) {
// int pieceSize = size - 20;
// int centerX = x + size / 2;
// int centerY = y + size / 2;

// if (player == Board.HUMAN) {
// // Blue piece for human
// g2d.setColor(Color.BLUE);
// g2d.fillOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize,
// pieceSize);
// g2d.setColor(Color.WHITE);
// g2d.drawOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize,
// pieceSize);
// g2d.setFont(new Font("Arial", Font.BOLD, 16));
// g2d.drawString("H", centerX - 5, centerY + 5);
// } else {
// // Red piece for AI
// g2d.setColor(Color.RED);
// g2d.fillOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize,
// pieceSize);
// g2d.setColor(Color.WHITE);
// g2d.drawOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize,
// pieceSize);
// g2d.setFont(new Font("Arial", Font.BOLD, 16));
// g2d.drawString("AI", centerX - 8, centerY + 5);
// }
// }

// private void drawSpecialSymbol(Graphics2D g2d, int x, int y, int size, int
// square) {
// g2d.setColor(Color.BLACK);
// g2d.setFont(new Font("Arial", Font.PLAIN, 10));

// String symbol = "";
// switch (square) {
// case 26:
// symbol = "😊";
// break; // Happiness
// case 27:
// symbol = "💧";
// break; // Water
// case 28:
// symbol = "❸";
// break; // 3 Truths
// case 29:
// symbol = "☀️";
// break; // Atum
// case 30:
// symbol = "👁️";
// break; // Horus
// }

// if (!symbol.isEmpty()) {
// FontMetrics fm = g2d.getFontMetrics();
// int symbolWidth = fm.stringWidth(symbol);
// g2d.drawString(symbol, x + size / 2 - symbolWidth / 2, y + size - 5);
// }
// }

// private void drawLegend(Graphics2D g2d, int x, int y) {
// g2d.setFont(new Font("Arial", Font.BOLD, 14));
// g2d.setColor(new Color(139, 69, 19));
// g2d.drawString("Legend:", x, y);

// y += 25;
// drawLegendItem(g2d, x, y, Color.BLUE, "Human Piece (H)");
// y += 25;
// drawLegendItem(g2d, x, y, Color.RED, "AI Piece (AI)");
// y += 25;
// drawLegendItem(g2d, x, y, specialColor, "Special Squares (26-30)");
// y += 25;
// drawLegendItem(g2d, x, y, safeColor, "Safe Squares (15, 27)");

// // Draw special symbols legend
// x += 250;
// y -= 75;
// g2d.setFont(new Font("Arial", Font.BOLD, 14));
// g2d.drawString("Special Symbols:", x, y);
// y += 25;
// g2d.setFont(new Font("Arial", Font.PLAIN, 12));
// g2d.drawString("😊 - House of Happiness (26)", x, y);
// y += 20;
// g2d.drawString("💧 - House of Water (27)", x, y);
// y += 20;
// g2d.drawString("❸ - House of 3 Truths (28)", x, y);
// y += 20;
// g2d.drawString("☀️ - House of Re-Atum (29)", x, y);
// y += 20;
// g2d.drawString("👁️ - House of Horus (30)", x, y);
// }

// private void drawLegendItem(Graphics2D g2d, int x, int y, Color color, String
// text) {
// g2d.setColor(color);
// g2d.fillRect(x, y - 10, 15, 15);
// g2d.setColor(Color.BLACK);
// g2d.drawRect(x, y - 10, 15, 15);
// g2d.setColor(Color.BLACK);
// g2d.setFont(new Font("Arial", Font.PLAIN, 12));
// g2d.drawString(text, x + 25, y);
// }
// }

// // Inner class for control panel
// class ControlPanel extends JPanel {
// private JButton rollButton;
// private JButton resetButton;
// private JPanel movesPanel;
// private JLabel statusLabel;

// public ControlPanel() {
// setLayout(new BorderLayout(10, 10));
// setPreferredSize(new Dimension(300, 0));
// setBackground(new Color(245, 222, 179));
// setBorder(BorderFactory.createCompoundBorder(
// BorderFactory.createTitledBorder(
// BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
// "Game Controls",
// TitledBorder.CENTER,
// TitledBorder.TOP,
// new Font("Arial", Font.BOLD, 14),
// new Color(139, 69, 19)),
// BorderFactory.createEmptyBorder(10, 10, 10, 10)));

// initializeComponents();
// }

// private void initializeComponents() {
// // Status label
// statusLabel = new JLabel("Human's Turn", SwingConstants.CENTER);
// statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
// statusLabel.setForeground(new Color(139, 69, 19));
// add(statusLabel, BorderLayout.NORTH);

// // Button panel
// JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
// buttonPanel.setBackground(new Color(245, 222, 179));

// rollButton = createStyledButton("Roll Sticks", new Color(34, 139, 34)); //
// Forest Green
// rollButton.addActionListener(e -> rollDiceForHuman());

// resetButton = createStyledButton("Reset Game", new Color(178, 34, 34)); //
// Firebrick
// resetButton.addActionListener(e -> resetGame());

// buttonPanel.add(rollButton);
// buttonPanel.add(resetButton);

// add(buttonPanel, BorderLayout.CENTER);

// // Moves panel
// movesPanel = new JPanel();
// movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
// movesPanel.setBackground(new Color(245, 222, 179));
// movesPanel.setBorder(BorderFactory.createTitledBorder("Available Moves"));

// JScrollPane scrollPane = new JScrollPane(movesPanel);
// scrollPane.setPreferredSize(new Dimension(280, 300));
// add(scrollPane, BorderLayout.SOUTH);
// }

// private JButton createStyledButton(String text, Color color) {
// JButton button = new JButton(text);
// button.setFont(new Font("Arial", Font.BOLD, 14));
// button.setBackground(color);
// button.setForeground(Color.WHITE);
// button.setFocusPainted(false);
// button.setBorder(BorderFactory.createCompoundBorder(
// BorderFactory.createLineBorder(color.darker(), 2),
// BorderFactory.createEmptyBorder(10, 20, 10, 20)));
// button.setCursor(new Cursor(Cursor.HAND_CURSOR));

// // Add hover effect
// button.addMouseListener(new MouseAdapter() {
// @Override
// public void mouseEntered(MouseEvent e) {
// button.setBackground(color.brighter());
// }

// @Override
// public void mouseExited(MouseEvent e) {
// button.setBackground(color);
// }
// });

// return button;
// }

// public void showMoveButtons(List<Move> moves) {
// movesPanel.removeAll();

// if (moves.isEmpty()) {
// JLabel noMovesLabel = new JLabel("No moves available");
// noMovesLabel.setForeground(Color.RED);
// movesPanel.add(noMovesLabel);
// } else {
// for (Move move : moves) {
// JButton moveButton = new JButton(move.toString());
// moveButton.setFont(new Font("Arial", Font.PLAIN, 12));
// moveButton.setBackground(new Color(240, 230, 140)); // Khaki
// moveButton.setBorder(BorderFactory.createCompoundBorder(
// BorderFactory.createLineBorder(new Color(184, 134, 11)),
// BorderFactory.createEmptyBorder(5, 10, 5, 10)));
// moveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

// moveButton.addActionListener(e -> {
// executeHumanMove(move);
// clearMoveButtons();
// });

// // Add hover effect to highlight the move on board
// moveButton.addMouseListener(new MouseAdapter() {
// @Override
// public void mouseEntered(MouseEvent e) {
// boardPanel.highlightMove(move);
// }

// @Override
// public void mouseExited(MouseEvent e) {
// boardPanel.highlightMove(null);
// }
// });

// movesPanel.add(moveButton);
// movesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
// }
// }

// movesPanel.revalidate();
// movesPanel.repaint();
// }

// public void clearMoveButtons() {
// movesPanel.removeAll();
// movesPanel.add(new JLabel("Roll to see available moves"));
// movesPanel.revalidate();
// movesPanel.repaint();
// boardPanel.highlightMove(null);
// }

// public void setGameControlsEnabled(boolean enabled) {
// rollButton.setEnabled(enabled);
// statusLabel.setText(enabled ? "Human's Turn" : "AI's Turn");
// statusLabel.setForeground(enabled ? new Color(34, 139, 34) : new Color(178,
// 34, 34));
// }
// }

// // Inner class for info panel
// class InfoPanel extends JPanel {
// private JLabel diceLabel;
// private JLabel playerLabel;
// private JLabel humanScoreLabel;
// private JLabel aiScoreLabel;
// private JTextArea messageArea;

// public InfoPanel() {
// setLayout(new BorderLayout(10, 10));
// setBackground(new Color(245, 222, 179));
// setBorder(BorderFactory.createCompoundBorder(
// BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
// BorderFactory.createEmptyBorder(10, 10, 10, 10)));
// setPreferredSize(new Dimension(0, 150));

// initializeComponents();
// }

// private void initializeComponents() {
// // Score panel
// JPanel scorePanel = new JPanel(new GridLayout(2, 2, 10, 10));
// scorePanel.setBackground(new Color(245, 222, 179));

// playerLabel = new JLabel("Current: Human", SwingConstants.CENTER);
// playerLabel.setFont(new Font("Arial", Font.BOLD, 14));
// playerLabel.setForeground(new Color(34, 139, 34));

// diceLabel = new JLabel("Last Roll: -", SwingConstants.CENTER);
// diceLabel.setFont(new Font("Arial", Font.BOLD, 16));
// diceLabel.setForeground(new Color(139, 69, 19));

// humanScoreLabel = new JLabel("Human Pieces Exited: 0/5",
// SwingConstants.CENTER);
// humanScoreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
// humanScoreLabel.setForeground(Color.BLUE);

// aiScoreLabel = new JLabel("AI Pieces Exited: 0/5", SwingConstants.CENTER);
// aiScoreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
// aiScoreLabel.setForeground(Color.RED);

// scorePanel.add(playerLabel);
// scorePanel.add(diceLabel);
// scorePanel.add(humanScoreLabel);
// scorePanel.add(aiScoreLabel);

// add(scorePanel, BorderLayout.NORTH);

// // Message area
// messageArea = new JTextArea(3, 40);
// messageArea.setEditable(false);
// messageArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
// messageArea.setBackground(new Color(255, 248, 220)); // Cornsilk
// messageArea.setBorder(BorderFactory.createCompoundBorder(
// BorderFactory.createLineBorder(new Color(184, 134, 11)),
// BorderFactory.createEmptyBorder(5, 5, 5, 5)));
// messageArea.setText("Welcome to Senet! Human starts the game.\nRoll the
// sticks to begin.");

// JScrollPane scrollPane = new JScrollPane(messageArea);
// add(scrollPane, BorderLayout.CENTER);
// }

// public void updateInfo(int currentPlayer, int humanExited, int aiExited) {
// playerLabel.setText(currentPlayer == Board.HUMAN ? "Current: Human" :
// "Current: AI");
// playerLabel.setForeground(currentPlayer == Board.HUMAN ? new Color(34, 139,
// 34) : new Color(178, 34, 34));

// humanScoreLabel.setText("Human Pieces Exited: " + humanExited + "/" +
// TOTAL_PIECES);
// aiScoreLabel.setText("AI Pieces Exited: " + aiExited + "/" + TOTAL_PIECES);
// }

// public void setDiceRoll(int roll) {
// diceLabel.setText("Last Roll: " + roll);

// // Visual effect for dice roll
// diceLabel.setForeground(new Color(255, 69, 0)); // Orange Red
// Timer timer = new Timer(500, e -> {
// diceLabel.setForeground(new Color(139, 69, 19));
// });
// timer.setRepeats(false);
// timer.start();
// }

// public void showMessage(String message) {
// messageArea.append("\n" + message);
// messageArea.setCaretPosition(messageArea.getDocument().getLength());
// }

// public void clearMessages() {
// messageArea.setText("");
// }
// }

// private void resetGame() {
// // Stop AI timer
// if (aiTimer != null && aiTimer.isRunning()) {
// aiTimer.stop();
// }

// // Reset game state
// initializeBoard();
// currentPlayer = Board.HUMAN;
// humanExited = 0;
// aiExited = 0;

// // Update UI
// updateBoardDisplay();
// updateInfoPanel();
// controlPanel.clearMoveButtons();
// controlPanel.setGameControlsEnabled(true);
// infoPanel.clearMessages();
// infoPanel.showMessage("Game reset. Human starts the game.");
// }

// public static void main(String[] args) {
// SwingUtilities.invokeLater(() -> {
// try {
// // Set system look and feel
// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

// // Set some default colors for better appearance
// UIManager.put("OptionPane.background", new Color(245, 222, 179));
// UIManager.put("Panel.background", new Color(245, 222, 179));

// } catch (Exception e) {
// e.printStackTrace();
// }

// new SenetGame();
// });
// }
// }