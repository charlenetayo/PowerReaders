import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

public class Theme {
    public static final Color INDIGO = new Color(0x4E5CD7);
    public static final Color NAVY = new Color(0x0F3566);
    public static final Color ORANGE = new Color(0xF9A72B);
    public static final Color LAVENDER = new Color(0xEEF0FC);

    // Orange filled button
    public static void primary(JButton b) {
        b.setBackground(ORANGE);
        b.setForeground(NAVY);
        setOutline(b, null);
    }

    // White button with indigo outline
    public static void outlined(JButton b) {
        b.setBackground(Color.WHITE);
        b.setForeground(INDIGO);
        setOutline(b, INDIGO);
    }

    // Indigo button with white outline, for the header
    public static void forIndigo(JButton b) {
        b.setBackground(INDIGO);
        b.setForeground(Color.WHITE);
        setOutline(b, Color.WHITE);
    }

    private static void setOutline(JButton b, Color c) {
        if (b instanceof RoundedButton) {
            ((RoundedButton) b).setOutline(c);
        }
    }
    
    public static <T> void combo(JComboBox<T> box) {
        box.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton() {
                    @Override
                    public void paint(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(Color.WHITE);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.setColor(INDIGO);
                        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int cx = getWidth() / 2, cy = getHeight() / 2;
                        g2.drawPolyline(new int[]{cx - 5, cx, cx + 5},
                                        new int[]{cy - 2, cy + 3, cy - 2}, 3);
                        g2.dispose();
                    }
                };
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setContentAreaFilled(false);
                b.setFocusPainted(false);
                return b;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle r, boolean hasFocus) {
                g.setColor(Color.WHITE);
                g.fillRect(r.x, r.y, r.width, r.height);
            }

            @Override
            protected ComboPopup createPopup() {
                BasicComboPopup popup = (BasicComboPopup) super.createPopup();
                popup.setBorder(BorderFactory.createLineBorder(INDIGO, 2));
                return popup;
            }
        });

        box.setBackground(Color.WHITE);
        box.setForeground(NAVY);
        box.setBorder(BorderFactory.createLineBorder(INDIGO, 2));

        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(
                        list, value, index, selected, focus);
                l.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                boolean highlight = selected && index >= 0;   // index -1 is the closed box itself
                l.setBackground(highlight ? INDIGO : Color.WHITE);
                l.setForeground(highlight ? Color.WHITE : NAVY);
                return l;
            }
        });
    }
}