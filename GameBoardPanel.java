import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class GameBoardPanel extends JPanel {
    private int[] currentBoard;
    private Color boardColor = new Color(205, 133, 63);
    private Color safeColor = new Color(154, 205, 50);
    private Color specialColor = new Color(218, 165, 32);
    private Color highlightColor = new Color(255, 215, 0, 128);
    private Move highlightedMove;

    public GameBoardPanel() {
        // Increased preferred size to accommodate larger squares
        setPreferredSize(new Dimension(1200, 850));
        setBackground(new Color(245, 222, 179));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 69, 19), 4),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
    }

    public void setBoard(int[] board) {
        // System.out.println("DEBUG: setBoard called");
        if (board != null) {
            this.currentBoard = board.clone();
        }
        repaint();
    }

    public void highlightMove(Move move) {
        // System.out.println("DEBUG: highlighting move: " + move);
        this.highlightedMove = move;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (currentBoard == null)
            return;

        // Increased square size from 80 to 100 for better visibility
        int squareSize = 100;
        int spacing = 12;
        int startX = 50;

        // Draw the 3 rows of the Senet board
        drawBoardRow(g2d, 0, 9, startX, 120, true, squareSize, spacing);
        drawBoardRow(g2d, 19, 10, startX, 120 + squareSize + spacing, false, squareSize, spacing);
        drawBoardRow(g2d, 20, 29, startX, 120 + (squareSize + spacing) * 2, true, squareSize, spacing);

        drawTitle(g2d);
        drawLegend(g2d, startX, 550);
    }

    private void drawBoardRow(Graphics2D g2d, int start, int end, int x, int y, boolean leftToRight, int size,
            int spacing) {
        int direction = leftToRight ? 1 : -1;
        int xPos = x;

        for (int i = start; leftToRight ? i <= end : i >= end; i += direction) {
            drawSquare(g2d, i, xPos, y, size);
            xPos += size + spacing;
        }
    }

    private void drawSquare(Graphics2D g2d, int index, int x, int y, int size) {
        Color squareColor = getSquareColor(index);
        g2d.setColor(squareColor);
        g2d.fillRoundRect(x, y, size, size, 10, 10);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x, y, size, size, 10, 10);

        // Highlight logic
        if (highlightedMove != null) {
            int fromIdx = highlightedMove.from - 1;
            int toIdx = (highlightedMove.to == -1) ? -1 : highlightedMove.to - 1;

            if ((fromIdx == index || toIdx == index) && fromIdx >= 0) {
                g2d.setColor(highlightColor);
                g2d.fillRoundRect(x, y, size, size, 10, 10);
            }
        }

        // Draw square number - increased font size
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString(String.valueOf(index + 1), x + 10, y + 25);

        // Draw piece if present
        if (index >= 0 && index < currentBoard.length && currentBoard[index] != 0) {
            drawPiece(g2d, x, y, size, currentBoard[index]);
        }

        // Labels for special squares with rules mentioned
        drawSpecialLabel(g2d, x, y, size, index + 1);
    }

    private Color getSquareColor(int index) {
        int sq = index + 1;
        if (sq == 26 || sq == 28 || sq == 29 || sq == 30)
            return specialColor;
        if (sq == 15 || sq == 27)
            return safeColor;
        return boardColor;
    }

    private void drawPiece(Graphics2D g2d, int x, int y, int size, int player) {
        int pSize = size - 40;
        int cx = x + size / 2;
        int cy = y + size / 2;

        if (player == 1) { // Human
            g2d.setColor(new Color(30, 144, 255));
            g2d.fillOval(cx - pSize / 2, cy - pSize / 2, pSize, pSize);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 24)); // Bigger text on piece
            g2d.drawString("H", cx - 9, cy + 9);
        } else if (player == -1) { // AI
            g2d.setColor(new Color(220, 20, 60));
            g2d.fillOval(cx - pSize / 2, cy - pSize / 2, pSize, pSize);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("AI", cx - 11, cy + 8);
        }
    }

    private void drawSpecialLabel(Graphics2D g2d, int x, int y, int size, int square) {
        g2d.setColor(new Color(40, 40, 40));
        g2d.setFont(new Font("Arial", Font.BOLD, 12)); // Bolded for better readability

        String line1 = "";
        String line2 = "";

        switch (square) {
            case 15:
                line1 = "REBIRTH";
                break;
            case 26:
                line1 = "HAPPINESS";
                line2 = "Landed exactly";
                break;
            case 27:
                line1 = "WATER";
                line2 = "Go to 15";
                break;
            case 28:
                line1 = "3 TRUTHS";
                line2 = "Need 3 to exit";
                break;
            case 29:
                line1 = "RE-ATUM";
                line2 = "Need 2 to exit";
                break;
            case 30:
                line1 = "HORUS";
                line2 = "Need 1 to exit";
                break;
        }

        if (!line1.isEmpty()) {
            FontMetrics fm = g2d.getFontMetrics();
            int l1x = x + (size - fm.stringWidth(line1)) / 2;
            g2d.drawString(line1, l1x, y + size - 25);

            if (!line2.isEmpty()) {
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                int l2x = x + (size - g2d.getFontMetrics().stringWidth(line2)) / 2;
                g2d.drawString(line2, l2x, y + size - 10);
            }
        }
    }

    private void drawTitle(Graphics2D g2d) {
        g2d.setFont(new Font("Serif", Font.BOLD, 36)); // Increased title size
        g2d.setColor(new Color(139, 69, 19));
        g2d.drawString("SENET - ANCIENT EGYPTIAN BOARD", 50, 60);
    }

    private void drawLegend(Graphics2D g2d, int x, int y) {
        y += 50; // Offset from the board
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.setColor(Color.BLACK);
        g2d.drawString("Rules & Info:", x, y);

        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString("- Blue (H): Human player pieces", x, y + 30);
        g2d.drawString("- Red (AI): Computer opponent pieces", x, y + 60);
        g2d.drawString("- Gold Squares (26-30): The final stretch. Follow specific exit rolls.", x + 400, y + 30);
        g2d.drawString("- Green Squares (15, 27): House of Rebirth (Safe) and House of Water (Trap).", x + 400, y + 60);

        // System.out.println("DEBUG: Legend rendered");
    }
}