
import javax.swing.*;
import java.awt.*;

public class GameBoardPanel extends JPanel {
    private int[] currentBoard;
    private Color boardColor = new Color(205, 133, 63);
    private Color safeColor = new Color(154, 205, 50);
    private Color specialColor = new Color(218, 165, 32);
    private Color highlightColor = new Color(255, 215, 0, 100);
    private Move highlightedMove;

    public GameBoardPanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(new Color(245, 222, 179));
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

        if (currentBoard == null)
            return;

        drawBoardRow(g2d, 0, 9, 50, 100, true);
        drawBoardRow(g2d, 19, 10, 50, 250, false);
        drawBoardRow(g2d, 20, 29, 50, 400, true);

        drawTitle(g2d);
        drawLegend(g2d, 50, 500);
    }

    private void drawBoardRow(Graphics2D g2d, int start, int end, int x, int y, boolean leftToRight) {
        int squareSize = 60;
        int spacing = 10;
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
        g2d.fillRoundRect(x, y, size, size, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, size, size, 10, 10);

        if (highlightedMove != null) {
            if (highlightedMove.from == index + 1 || highlightedMove.to == index + 1) {
                g2d.setColor(highlightColor);
                g2d.fillRoundRect(x, y, size, size, 10, 10);
            }
        }

        drawSquareNumber(g2d, index, x, y);

        if (currentBoard[index] != Board.EMPTY) {
            drawPiece(g2d, x, y, size, currentBoard[index]);
        }

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
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(String.valueOf(index + 1), x + 5, y + 15);
    }

    private void drawPiece(Graphics2D g2d, int x, int y, int size, int player) {
        int pieceSize = size - 20;
        int centerX = x + size / 2;
        int centerY = y + size / 2;

        if (player == Board.HUMAN) {
            g2d.setColor(Color.BLUE);
            g2d.fillOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize, pieceSize);
            g2d.setColor(Color.WHITE);
            g2d.drawOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize, pieceSize);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("H", centerX - 5, centerY + 5);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize, pieceSize);
            g2d.setColor(Color.WHITE);
            g2d.drawOval(centerX - pieceSize / 2, centerY - pieceSize / 2, pieceSize, pieceSize);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("AI", centerX - 8, centerY + 5);
        }
    }

    private void drawSpecialSymbol(Graphics2D g2d, int x, int y, int size, int square) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        String symbol = getSymbolForSquare(square);
        if (!symbol.isEmpty()) {
            FontMetrics fm = g2d.getFontMetrics();
            int symbolWidth = fm.stringWidth(symbol);
            g2d.drawString(symbol, x + size / 2 - symbolWidth / 2, y + size - 5);
        }
    }

    private String getSymbolForSquare(int square) {
        switch (square) {
            case 26:
                return "😊";
            case 27:
                return "💧";
            case 28:
                return "❸";
            case 29:
                return "☀️";
            case 30:
                return "👁️";
            default:
                return "";
        }
    }

    private void drawTitle(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(new Color(139, 69, 19));
        String title = "SENET - Ancient Egyptian Game";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, (getWidth() - titleWidth) / 2, 40);
    }

    private void drawLegend(Graphics2D g2d, int x, int y) {
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.setColor(new Color(139, 69, 19));
        g2d.drawString("Legend:", x, y);

        y += 25;
        drawLegendItem(g2d, x, y, Color.BLUE, "Human Piece (H)");
        y += 25;
        drawLegendItem(g2d, x, y, Color.RED, "AI Piece (AI)");
        y += 25;
        drawLegendItem(g2d, x, y, specialColor, "Special Squares (26-30)");
        y += 25;
        drawLegendItem(g2d, x, y, safeColor, "Safe Squares (15, 27)");

        x += 250;
        y -= 75;
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Special Symbols:", x, y);
        y += 25;
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("😊 - House of Happiness (26)", x, y);
        y += 20;
        g2d.drawString("💧 - House of Water (27)", x, y);
        y += 20;
        g2d.drawString("❸ - House of 3 Truths (28)", x, y);
        y += 20;
        g2d.drawString("☀️ - House of Re-Atum (29)", x, y);
        y += 20;
        g2d.drawString("👁️ - House of Horus (30)", x, y);
    }

    private void drawLegendItem(Graphics2D g2d, int x, int y, Color color, String text) {
        g2d.setColor(color);
        g2d.fillRect(x, y - 10, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y - 10, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(text, x + 25, y);
    }
}