import javax.swing.*;
import java.awt.*;

/*
    WideButton
    Pulsante stilizzato che occupa tutto lo spazio orizzontale del contenitore
    Usabile anche come label
*/
public class WideButton extends JButton {
    private boolean selected = false;

    public WideButton(String label) {
        super(label);
        super.setContentAreaFilled(false);
        super.setBorderPainted(false);
        setForeground(Colors.TEXT_COLOR);
        setFocusPainted(false);
        setFont(new Font("Dialog", Font.PLAIN, 24));
        setIconTextGap(20);
    }

    public void setSelected(boolean value) {
        selected = value;
    }

    public Dimension getPreferredSize() {
        Dimension parentDimension = getParent().getSize();
        return new Dimension((int)(parentDimension.getWidth() - parentDimension.getWidth() * 0.1), 64);
    }

    public void paintComponent(Graphics g) {
        if(selected) {
            setForeground(Colors.TEXT_LIGHT_COLOR);
            g.setColor(Colors.ACCENT_COLOR);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        super.paintComponent(g);

        if (getModel().isRollover())
            setForeground(Colors.TEXT_LIGHT_COLOR);
        else
            setForeground(Colors.TEXT_COLOR);

    }

}