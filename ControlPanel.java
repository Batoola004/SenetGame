
// //كلاس واجهات
// import javax.swing.*;
// import javax.swing.border.*;
// import java.awt.*;
// import java.awt.event.*;
// import java.util.List;

// public class ControlPanel extends JPanel {
//     private JButton rollButton;
//     private JButton resetButton;
//     private JPanel movesPanel;
//     private JLabel statusLabel;
//     private SenetGame game;

//     public ControlPanel(SenetGame game) {
//         this.game = game;

//         setLayout(new BorderLayout(10, 10));
//         setPreferredSize(new Dimension(300, 0));
//         setBackground(new Color(245, 222, 179));
//         setBorder(createBorder());

//         initializeComponents();

//     }

//     private Border createBorder() {
//         return BorderFactory.createCompoundBorder(
//                 BorderFactory.createTitledBorder(
//                         BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
//                         "Game Controls",
//                         TitledBorder.CENTER,
//                         TitledBorder.TOP,
//                         new Font("Arial", Font.BOLD, 14),
//                         new Color(139, 69, 19)),
//                 BorderFactory.createEmptyBorder(10, 10, 10, 10));
//     }

//     private void initializeComponents() {
//         statusLabel = new JLabel("Human Turn", SwingConstants.CENTER);
//         statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
//         statusLabel.setForeground(new Color(139, 69, 19));
//         add(statusLabel, BorderLayout.NORTH);

//         JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
//         buttonPanel.setBackground(new Color(245, 222, 179));

//         rollButton = createStyledButton("Roll Sticks", new Color(34, 139, 34));
//         rollButton.addActionListener(e -> {
//             game.rollDiceForHuman();
//         });

//         resetButton = createStyledButton("Reset Game", new Color(178, 34, 34));
//         resetButton.addActionListener(e -> {
//             game.resetGame();
//         });

//         buttonPanel.add(rollButton);
//         buttonPanel.add(resetButton);
//         add(buttonPanel, BorderLayout.CENTER);

//         movesPanel = new JPanel();
//         movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
//         movesPanel.setBackground(new Color(245, 222, 179));
//         movesPanel.setBorder(BorderFactory.createTitledBorder("Available Moves"));

//         JScrollPane scrollPane = new JScrollPane(movesPanel);
//         scrollPane.setPreferredSize(new Dimension(280, 300));
//         add(scrollPane, BorderLayout.SOUTH);
//     }

//     private JButton createStyledButton(String text, Color color) {
//         JButton button = new JButton(text);
//         button.setFont(new Font("Arial", Font.BOLD, 14));
//         button.setBackground(color);
//         button.setForeground(Color.WHITE);
//         button.setFocusPainted(false);
//         button.setBorder(BorderFactory.createCompoundBorder(
//                 BorderFactory.createLineBorder(color.darker(), 2),
//                 BorderFactory.createEmptyBorder(10, 20, 10, 20)));
//         button.setCursor(new Cursor(Cursor.HAND_CURSOR));

//         button.addMouseListener(new MouseAdapter() {
//             @Override
//             public void mouseEntered(MouseEvent e) {
//                 button.setBackground(color.brighter());
//             }

//             @Override
//             public void mouseExited(MouseEvent e) {
//                 button.setBackground(color);
//             }
//         });

//         return button;
//     }

//     public void showMoveButtons(List<Move> moves) {
//         movesPanel.removeAll();

//         if (moves.isEmpty()) {
//             JLabel noMovesLabel = new JLabel("No moves available");
//             noMovesLabel.setForeground(Color.RED);
//             movesPanel.add(noMovesLabel);
//         } else {
//             for (Move move : moves) {
//                 JButton moveButton = createMoveButton(move);
//                 movesPanel.add(moveButton);
//                 movesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
//             }
//         }

//         movesPanel.revalidate();
//         movesPanel.repaint();
//     }

//     private JButton createMoveButton(Move move) {
//         JButton button = new JButton(move.toString());
//         button.setFont(new Font("Arial", Font.PLAIN, 12));
//         button.setBackground(new Color(240, 230, 140));
//         button.setBorder(BorderFactory.createCompoundBorder(
//                 BorderFactory.createLineBorder(new Color(184, 134, 11)),
//                 BorderFactory.createEmptyBorder(5, 10, 5, 10)));
//         button.setCursor(new Cursor(Cursor.HAND_CURSOR));

//         button.addActionListener(e -> {
//             // System.out.println("DEBUG: Move button clicked: " + move);
//             game.executeHumanMove(move);
//             clearMoveButtons();
//         });

//         return button;
//     }

//     public void clearMoveButtons() {
//         movesPanel.removeAll();
//         movesPanel.add(new JLabel("Roll to see available moves"));
//         movesPanel.revalidate();
//         movesPanel.repaint();
//     }

//     public void setGameControlsEnabled(boolean enabled) {
//         // System.out.println("DEBUG: Setting controls enabled to: " + enabled);
//         rollButton.setEnabled(enabled);
//         statusLabel.setText(enabled ? "Human Turn" : "AI Turn");
//         statusLabel.setForeground(enabled ? new Color(34, 139, 34) : new Color(178, 34, 34));
//     }
// }
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ControlPanel extends JPanel {
    private JButton rollButton;
    private JButton resetButton;
    private JPanel movesPanel;
    private JLabel statusLabel;
    private SenetGame game;

    public ControlPanel(SenetGame game) {
        this.game = game;

        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(300, 0));
        setBackground(new Color(245, 222, 179));
        setBorder(createBorder());

        initializeComponents();

    }

    // إنشاء الإطار (Border) مع تنسيق أنيق
    private Border createBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(139, 69, 19), 2),
                        "Game Controls",
                        TitledBorder.CENTER,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        new Color(139, 69, 19)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // تهيئة جميع المكونات الأساسية في لوحة التحكم
    private void initializeComponents() {
        statusLabel = new JLabel("Human Turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setForeground(new Color(139, 69, 19));
        add(statusLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(new Color(245, 222, 179));

        rollButton = createStyledButton("Roll Sticks", new Color(34, 139, 34));
        rollButton.addActionListener(e -> {
            game.rollDiceForHuman();
        });

        resetButton = createStyledButton("Reset Game", new Color(178, 34, 34));
        resetButton.addActionListener(e -> {
            game.resetGame();
        });

        buttonPanel.add(rollButton);
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.CENTER);

        movesPanel = new JPanel();
        movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
        movesPanel.setBackground(new Color(245, 222, 179));
        movesPanel.setBorder(BorderFactory.createTitledBorder("Available Moves"));

        JScrollPane scrollPane = new JScrollPane(movesPanel);
        scrollPane.setPreferredSize(new Dimension(280, 300));
        add(scrollPane, BorderLayout.SOUTH);
    }

    // إنشاء أزرار بتنسيق موحد مع تأثيرات عند المرور بالماوس
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // تغيير لون الزر عند المرور عليه بالماوس
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        // تغيير لون الزر عند تعطيله
        button.addPropertyChangeListener("enabled", evt -> {
            if (!button.isEnabled()) {
                button.setBackground(Color.GRAY);
                button.setToolTipText("Wait for your turn");
            } else {
                button.setBackground(color);
                button.setToolTipText(null);
            }
        });

        return button;
    }

    // تمكين أو تعطيل زر الرمي فقط (دالة مساعدة)
    public void setRollButtonEnabled(boolean enabled) {
        rollButton.setEnabled(enabled);
    }

    // عرض أزرار الحركات الممكنة للمستخدم
    public void showMoveButtons(List<Move> moves) {
        movesPanel.removeAll();

        if (moves.isEmpty()) {
            JLabel noMovesLabel = new JLabel("No moves available");
            noMovesLabel.setForeground(Color.RED);
            movesPanel.add(noMovesLabel);
        } else {
            for (Move move : moves) {
                JButton moveButton = createMoveButton(move);
                movesPanel.add(moveButton);
                movesPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        movesPanel.revalidate();
        movesPanel.repaint();
    }

    // إنشاء زر خاص لكل حركة ممكنة
    private JButton createMoveButton(Move move) {
        JButton button = new JButton(move.toString());
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBackground(new Color(240, 230, 140));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(184, 134, 11)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 255, 200));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(240, 230, 140));
            }
        });

        button.addActionListener(e -> {
            game.executeHumanMove(move); // تنفيذ الحركة المختارة
            clearMoveButtons(); // مسح جميع أزرار الحركات
            setRollButtonEnabled(true); // إعادة تمكين زر الرمي للدور التالي
        });

        return button;
    }

    // مسح جميع أزرار الحركات من اللوحة
    public void clearMoveButtons() {
        movesPanel.removeAll();
        movesPanel.add(new JLabel("Roll to see available moves"));
        movesPanel.revalidate();
        movesPanel.repaint();
    }

    // تمكين أو تعطيل جميع عناصر التحكم مع تحديث النص واللون
    public void setGameControlsEnabled(boolean enabled) {
        statusLabel.setText(enabled ? "Human Turn" : "AI Turn");
        statusLabel.setForeground(enabled ? new Color(34, 139, 34) : new Color(178, 34, 34));
    }
}