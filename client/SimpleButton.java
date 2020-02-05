import javax.swing.*;
import java.awt.*;

/*
    SimpleButton
    Semplice pulsante, un po' piu` stilizzato
*/
public class SimpleButton extends JButton {
    private Color bgColor;

    public SimpleButton(String label) {
        super(label);
        super.setContentAreaFilled(false);
        super.setBorderPainted(false);
        setForeground(Colors.TEXT_COLOR);
        setFocusPainted(false);
        setFont(new Font("Dialog", Font.PLAIN, 24));
        setIconTextGap(20);
        bgColor = Colors.BUTTON_COLOR;
    }

    public SimpleButton() {
        this("");
    }

    private boolean revert = false;
    public void paintComponent(Graphics g) {
        g.setColor(bgColor);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);


        if (getModel().isRollover()) {
            setForeground(Colors.TEXT_LIGHT_COLOR);
            bgColor = Colors.BUTTON_COLOR.brighter();
        } else {
            setForeground(Colors.TEXT_COLOR);
            bgColor = Colors.BUTTON_COLOR;
        }

        if(getModel().isPressed()) {
            bgColor = Colors.ACCENT_COLOR;
        }

    }
}