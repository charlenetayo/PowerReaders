import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {
    private final int arc;
    private Color outline = null;

    public RoundedButton(String text, int arc) {
        super(text);
        this.arc = arc;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setOutline(Color c) {
        this.outline = c;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!isEnabled()) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        Color fill = getBackground();
        if (isEnabled() && getModel().isPressed()) {
            fill = fill.darker();
        } else if (isEnabled() && getModel().isRollover()) {
            fill = blend(fill, Color.BLACK, 0.06f);
        }

        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        if (outline != null) {
            g2.setColor(outline);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
        }
        g2.dispose();
        super.paintComponent(g);   // draws the text
    }

    private static Color blend(Color a, Color b, float amount) {
        int r = Math.round(a.getRed()   * (1 - amount) + b.getRed()   * amount);
        int gr = Math.round(a.getGreen() * (1 - amount) + b.getGreen() * amount);
        int bl = Math.round(a.getBlue()  * (1 - amount) + b.getBlue()  * amount);
        return new Color(r, gr, bl);
    }
}