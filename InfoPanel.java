
//كلاس واجهات
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.time.format.DateTimeFormatter;

public class InfoPanel extends JPanel {
    private JLabel diceLabel;
    private JLabel playerLabel;
    private JLabel humanScoreLabel;
    private JLabel aiScoreLabel;
    private JLabel aiEvaluationLabel;
    private JTextArea messageArea;
    private JScrollPane scrollPane;

    public InfoPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 222, 179));
        setBorder(createBorder());
        setPreferredSize(new Dimension(0, 200)); // Increased height

        initializeComponents();
    }

    private Border createBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(139, 69, 19), 3),
                        "Game Information",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(139, 69, 19)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void initializeComponents() {
        // Top panel for scores and player info
        JPanel topPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        topPanel.setBackground(new Color(245, 222, 179));

        playerLabel = new JLabel("Current Player: Human", SwingConstants.CENTER);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        playerLabel.setForeground(new Color(34, 139, 34));
        topPanel.add(playerLabel);

        diceLabel = new JLabel("Last Roll: -", SwingConstants.CENTER);
        diceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        diceLabel.setForeground(new Color(139, 69, 19));
        topPanel.add(diceLabel);

        // Game status panel
        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        statusPanel.setBackground(new Color(245, 222, 179));

        humanScoreLabel = new JLabel("Human Pieces Exited: 0/5", SwingConstants.CENTER);
        humanScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        humanScoreLabel.setForeground(Color.BLUE);
        statusPanel.add(humanScoreLabel);

        aiScoreLabel = new JLabel("AI Pieces Exited: 0/5", SwingConstants.CENTER);
        aiScoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        aiScoreLabel.setForeground(Color.RED);
        statusPanel.add(aiScoreLabel);

        topPanel.add(statusPanel);

        add(topPanel, BorderLayout.NORTH);

        // AI Evaluation panel
        JPanel evalPanel = new JPanel();
        evalPanel.setBackground(new Color(245, 222, 179));
        evalPanel.setLayout(new BoxLayout(evalPanel, BoxLayout.Y_AXIS));

        JLabel evalTitle = new JLabel("AI Evaluation:");
        evalTitle.setFont(new Font("Arial", Font.BOLD, 16));
        evalTitle.setForeground(new Color(139, 69, 19));
        evalPanel.add(evalTitle);

        aiEvaluationLabel = new JLabel("Waiting for AI to think...");
        aiEvaluationLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        aiEvaluationLabel.setForeground(new Color(178, 34, 34));
        evalPanel.add(aiEvaluationLabel);

        add(evalPanel, BorderLayout.CENTER);

        // Message area
        messageArea = new JTextArea(4, 50);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        messageArea.setBackground(new Color(255, 250, 240)); // Floral White
        messageArea.setForeground(new Color(101, 67, 33)); // Dark Brown
        messageArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(184, 134, 11), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
                "Game Log",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                new Color(139, 69, 19)));

        add(scrollPane, BorderLayout.SOUTH);
    }

    public void updateInfo(int currentPlayer, int humanExited, int aiExited) {
        String playerText = currentPlayer == Board.HUMAN ? "Human" : "AI";
        Color playerColor = currentPlayer == Board.HUMAN ? new Color(34, 139, 34) : new Color(178, 34, 34);

        playerLabel.setText("Current Player: " + playerText);
        playerLabel.setForeground(playerColor);

        humanScoreLabel.setText("Human Pieces Exited: " + humanExited + "/5");
        aiScoreLabel.setText("AI Pieces Exited: " + aiExited + "/5");

        // Update background color based on whose turn it is
        setBackground(currentPlayer == Board.HUMAN ? new Color(245, 222, 179, 200) : new Color(255, 228, 181, 200)); // Lighter
                                                                                                                     // background
                                                                                                                     // for
                                                                                                                     // AI
                                                                                                                     // turn
    }

    public void setDiceRoll(int roll) {
        diceLabel.setText("Last Roll: " + roll);
        diceLabel.setForeground(new Color(255, 69, 0)); // Orange Red

        // Flash effect
        Timer flashTimer = new Timer(300, e -> {
            diceLabel.setForeground(new Color(139, 69, 19));
        });
        flashTimer.setRepeats(false);
        flashTimer.start();

        // Log the roll
        showMessage("🎲 Dice rolled: " + roll);
    }

    public void setAIEvaluation(double evaluation, int nodesVisited) {
        String evalText = String.format("Value: %.2f | Nodes: %d", evaluation, nodesVisited);
        aiEvaluationLabel.setText(evalText);

        // Color code based on evaluation value
        if (evaluation > 100) {
            aiEvaluationLabel.setForeground(new Color(34, 139, 34)); // Green - AI is winning
        } else if (evaluation < -100) {
            aiEvaluationLabel.setForeground(new Color(178, 34, 34)); // Red - AI is losing
        } else {
            aiEvaluationLabel.setForeground(new Color(139, 69, 19)); // Brown - neutral
        }

        // Show thinking animation
        aiEvaluationLabel.setText("🧠 AI thinking...");
        Timer thinkingTimer = new Timer(1000, e -> {
            aiEvaluationLabel.setText(evalText);
        });
        thinkingTimer.setRepeats(false);
        thinkingTimer.start();
    }

    public void showMessage(String message) {
        String timestamp = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        messageArea.append("[" + timestamp + "] " + message + "\n");
        messageArea.setCaretPosition(messageArea.getDocument().getLength());

        // Auto-scroll to bottom
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(
                    scrollPane.getVerticalScrollBar().getMaximum());
        });
    }

    public void clearMessages() {
        messageArea.setText("");
        showMessage("Game started. Human begins.");
    }

    public void showWinMessage(String winner) {
        String winMessage = "🏆 " + winner + " WINS! All pieces have exited the board!";
        showMessage(winMessage);

        // Flash win message
        Timer flashTimer = new Timer(500, new ActionListener() {
            boolean on = true;
            int count = 0;

            public void actionPerformed(ActionEvent e) {
                if (count > 6) {
                    ((Timer) e.getSource()).stop();
                    return;
                }
                playerLabel.setForeground(on ? Color.GREEN : Color.YELLOW);
                on = !on;
                count++;
            }
        });
        flashTimer.start();
    }

}