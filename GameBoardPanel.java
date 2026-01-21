import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class GameBoardPanel extends JPanel {
    private int[] currentBoard;
    private Color boardColor = new Color(205, 133, 63); // Peru
    private Color safeColor = new Color(154, 205, 50); // Yellow Green
    private Color specialColor = new Color(218, 165, 32); // Goldenrod
    private Color highlightColor = new Color(255, 215, 0, 128); // Gold with transparency
    private Move highlightedMove;

    public GameBoardPanel() {
        setPreferredSize(new Dimension(1000, 750)); // Increased size
        setBackground(new Color(245, 222, 179)); // Wheat
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(139, 69, 19), 4),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
    }

    public void setBoard(int[] board) {
        this.currentBoard = board.clone();
        repaint();
    }

    public void highlightMove(Move move) {
        this.highlightedMove = move;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (currentBoard == null) {
            return;
        }

        // Draw the board in 3 rows
        drawBoardRow(g2d, 0, 9, 100, 120, true); // Row 1: 1-10
        drawBoardRow(g2d, 19, 10, 100, 300, false); // Row 2: 20-11
        drawBoardRow(g2d, 20, 29, 100, 480, true); // Row 3: 21-30

        // Draw title
        drawTitle(g2d);

        // Draw legend
        drawLegend(g2d, 100, 650);
    }

    private void drawBoardRow(Graphics2D g2d, int start, int end, int x, int y, boolean leftToRight) {
        int squareSize = 80; // Increased size
        int spacing = 15;
        int direction = leftToRight ? 1 : -1;
        int xPos = x;

        for (int i = start; leftToRight ? i <= end : i >= end; i += direction) {
            drawSquare(g2d, i, xPos, y, squareSize);
            xPos += squareSize + spacing;
        }
    }

    private void drawSquare(Graphics2D g2d, int index, int x, int y, int size) {
        Color squareColor = getSquareColor(index);
        g2d.setColor(squareColor);
        g2d.fillRoundRect(x, y, size, size, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, size, size, 15, 15);

        // Highlight if this is part of a move
        if (highlightedMove != null) {
            int fromIndex = highlightedMove.from - 1;
            int toIndex = (highlightedMove.to == -1) ? -1 : highlightedMove.to - 1;

            if ((fromIndex == index || toIndex == index) && fromIndex >= 0 && toIndex != -2) {
                g2d.setColor(highlightColor);
                g2d.fillRoundRect(x, y, size, size, 15, 15);

                // Draw arrow between squares if both are valid
                if (fromIndex >= 0 && toIndex >= 0 && fromIndex != toIndex) {
                    g2d.setColor(Color.RED);
                    g2d.setStroke(new BasicStroke(3));

                    int fromX = x + size / 2;
                    int fromY = y + size / 2;
                    int toX, toY;

                    // This is a simplified arrow drawing - in a real implementation
                    // you'd need to calculate the actual positions of both squares
                    toX = fromX + 40;
                    toY = fromY;

                    g2d.drawLine(fromX, fromY, toX, toY);

                    // Draw arrowhead
                    int arrowSize = 10;
                    g2d.fillPolygon(new int[] { toX, toX - arrowSize, toX - arrowSize },
                            new int[] { toY, toY - arrowSize, toY + arrowSize }, 3);
                }
            }
        }

        // Draw square number
        drawSquareNumber(g2d, index, x, y);

        // Draw piece if present
        if (index >= 0 && index < currentBoard.length && currentBoard[index] != Board.EMPTY) {
            drawPiece(g2d, x, y, size, currentBoard[index]);
        }

        // Draw special square symbols
        drawSpecialSymbol(g2d, x, y, size, index + 1);
    }

    private Color getSquareColor(int index) {
        int square = index + 1;
        if (square == Rules.HOUSE_OF_HAPPINESS ||
                square == Rules.HOUSE_OF_THREE_TRUTHS ||
                square == Rules.HOUSE_OF_RE_ATUM ||
                square == Rules.HOUSE_OF_HORUS) {
            return specialColor;
        } else if (square == Rules.HOUSE_OF_REBIRTH || square == Rules.HOUSE_OF_WATER) {
            return safeColor;
        } else {
            return boardColor;
        }
    }

    private void drawSquareNumber(Graphics2D g2d, int index, int x, int y) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString(String.valueOf(index + 1), x + 8, y + 25);
    }

    private void drawPiece(Graphics2D g2d, int x, int y, int size, int player) {
        int pieceSize = size - 30;
        int centerX = x + size / 2;
        int centerY = y + size / 2;

        if (player == Board.HUMAN) {
            // Blue piece for human
            g2d.setColor(new Color(30, 144, 255)); // Dodger Blue
            g2d.fillOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize, pieceSize);
            g2d.setColor(Color.WHITE);
            g2d.drawOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize, pieceSize);
            g2d.setStroke(new BasicStroke(2));
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("H", centerX - 8, centerY + 8);
        } else {
            // Red piece for AI
            g2d.setColor(new Color(220, 20, 60)); // Crimson
            g2d.fillOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize, pieceSize);
            g2d.setColor(Color.WHITE);
            g2d.drawOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize, pieceSize);
            g2d.setStroke(new BasicStroke(2));
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("AI", centerX - 12, centerY + 6);
        }
    }

    private void drawSpecialSymbol(Graphics2D g2d, int x, int y, int size, int square) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        String symbol = getSymbolForSquare(square);

        if (!symbol.isEmpty()) {
            FontMetrics fm = g2d.getFontMetrics();
            int symbolWidth = fm.stringWidth(symbol);
            g2d.drawString(symbol, x + size / 2 - symbolWidth / 2, y + size - 8);
        }
    }

    private String getSymbolForSquare(int square) {
        switch (square) {
            case 26:
                return "😊"; // House of Happiness
            case 27:
                return "💧"; // House of Water
            case 28:
                return "❸"; // House of Three Truths
            case 29:
                return "☀️"; // House of Re-Atum
            case 30:
                return "👁️"; // House of Horus
            default:
                return "";
        }
    }

    private void drawTitle(Graphics2D g2d) {
        g2d.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 32));
        g2d.setColor(new Color(139, 69, 19));
        String title = "SENET - Ancient Egyptian Game";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, (getWidth() - titleWidth) / 2, 60);

        // Draw decorative border under title
        g2d.setColor(new Color(218, 165, 32));
        g2d.setStroke(new BasicStroke(3));
        int startY = 75;
        g2d.drawLine(50, startY, getWidth() - 50, startY);
    }

    private void drawLegend(Graphics2D g2d, int x, int y) {
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.setColor(new Color(139, 69, 19));
        g2d.drawString("Game Legend:", x, y);
        y += 40;

        drawLegendItem(g2d, x, y, new Color(30, 144, 255), "Human Player (H)");
        y += 35;
        drawLegendItem(g2d, x, y, new Color(220, 20, 60), "AI Player (AI)");
        y += 35;
        drawLegendItem(g2d, x, y, specialColor, "Special Squares (26-30)");
        y += 35;
        drawLegendItem(g2d, x, y, safeColor, "Safe Squares (15, 27)");

        // Draw special symbols legend
        x += 450;
        y -= 120;
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Special Square Effects:", x, y);
        y += 40;

        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString("😊 House of Happiness (26) - Must land exactly", x, y);
        y += 30;
        g2d.drawString("💧 House of Water (27) - Return to House of Rebirth", x, y);
        y += 30;
        g2d.drawString("❸ House of Three Truths (28) - Exit with roll of 3", x, y);
        y += 30;
        g2d.drawString("☀️ House of Re-Atum (29) - Exit with roll of 2", x, y);
        y += 30;
        g2d.drawString("👁️ House of Horus (30) - Exit with roll of 1", x, y);
    }

    private void drawLegendItem(Graphics2D g2d, int x, int y, Color color, String text) {
        g2d.setColor(color);
        g2d.fillOval(x, y - 20, 30, 30);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x, y - 20, 30, 30);

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        g2d.drawString(text, x + 40, y);
    }
}