
import javax.swing.*;
import java.awt.*;

public class SquareButton extends JButton {
    private int squareNumber;
    private int occupant;
    private boolean isSpecial;
    private boolean isSafe;

    public SquareButton(int squareNumber, int occupant) {
        this.squareNumber = squareNumber;
        this.occupant = occupant;
        this.isSpecial = isSpecialSquare(squareNumber);
        this.isSafe = isSafeSquare(squareNumber);

        initializeButton();
    }

    private void initializeButton() {
        setPreferredSize(new Dimension(60, 60));
        setFont(new Font("Arial", Font.BOLD, 12));

        setBackground(getButtonColor());
        setText(getButtonText());
        setForeground(getTextColor());

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        setFocusPainted(false);
    }

    private Color getButtonColor() {
        if (isSpecial) {
            return new Color(218, 165, 32);
        } else if (isSafe) {
            return new Color(154, 205, 50);
        } else {
            return new Color(205, 133, 63);
        }
    }

    private Color getTextColor() {
        if (occupant != Board.EMPTY) {
            return Color.WHITE;
        }
        return Color.BLACK;
    }

    private String getButtonText() {
        if (occupant == Board.HUMAN) {
            return "H";
        } else if (occupant == Board.AI) {
            return "AI";
        }
        return String.valueOf(squareNumber);
    }

    private boolean isSpecialSquare(int square) {
        return square == Rules.HOUSE_OF_HAPPINESS ||
                square == Rules.HOUSE_OF_THREE_TRUTHS ||
                square == Rules.HOUSE_OF_RE_ATUM ||
                square == Rules.HOUSE_OF_HORUS;
    }

    private boolean isSafeSquare(int square) {
        return square == Rules.HOUSE_OF_REBIRTH ||
                square == Rules.HOUSE_OF_WATER;
    }

    public void setOccupant(int occupant) {
        this.occupant = occupant;
        setText(getButtonText());
        setForeground(getTextColor());
        repaint();
    }

    public int getSquareNumber() {
        return squareNumber;
    }

    public int getOccupant() {
        return occupant;
    }
}