
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class InfoPanel extends JPanel {
    private JLabel diceLabel;
    private JLabel playerLabel;
    private JLabel humanScoreLabel;
    private JLabel aiScoreLabel;
    private JTextArea messageArea;

    public InfoPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 222, 179));
        setBorder(createBorder());
        setPreferredSize(new Dimension(0, 150));

        initializeComponents();
    }

    private Border createBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void initializeComponents() {
        JPanel scorePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        scorePanel.setBackground(new Color(245, 222, 179));

        playerLabel = new JLabel("Current: Human", SwingConstants.CENTER);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        playerLabel.setForeground(new Color(34, 139, 34));

        diceLabel = new JLabel("Last Roll: -", SwingConstants.CENTER);
        diceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        diceLabel.setForeground(new Color(139, 69, 19));

        humanScoreLabel = new JLabel("Human Pieces Exited: 0/5", SwingConstants.CENTER);
        humanScoreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        humanScoreLabel.setForeground(Color.BLUE);

        aiScoreLabel = new JLabel("AI Pieces Exited: 0/5", SwingConstants.CENTER);
        aiScoreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        aiScoreLabel.setForeground(Color.RED);

        scorePanel.add(playerLabel);
        scorePanel.add(diceLabel);
        scorePanel.add(humanScoreLabel);
        scorePanel.add(aiScoreLabel);

        add(scorePanel, BorderLayout.NORTH);

        messageArea = new JTextArea(3, 40);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        messageArea.setBackground(new Color(255, 248, 220));
        messageArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(184, 134, 11)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JScrollPane scrollPane = new JScrollPane(messageArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateInfo(int currentPlayer, int humanExited, int aiExited) {
        String playerText = currentPlayer == Board.HUMAN ? "Human" : "AI";
        Color playerColor = currentPlayer == Board.HUMAN ? new Color(34, 139, 34) : new Color(178, 34, 34);

        playerLabel.setText("Current: " + playerText);
        playerLabel.setForeground(playerColor);

        humanScoreLabel.setText("Human Pieces Exited: " + humanExited + "/5");
        aiScoreLabel.setText("AI Pieces Exited: " + aiExited + "/5");
    }

    public void setDiceRoll(int roll) {
        diceLabel.setText("Last Roll: " + roll);
        diceLabel.setForeground(new Color(255, 69, 0));

        Timer timer = new Timer(500, e -> {
            diceLabel.setForeground(new Color(139, 69, 19));
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void showMessage(String message) {
        messageArea.append("\n" + message);
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    public void clearMessages() {
        messageArea.setText("");
    }
}