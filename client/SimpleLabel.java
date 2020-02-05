import javax.swing.*;
import java.awt.*;

/*
    SimpleButton
    Semplice label, un po' piu` stilizzata
*/
public class SimpleLabel extends JLabel{

    public SimpleLabel(String label) {
        super(label);
        setForeground(Colors.TEXT_COLOR);
        setFont(new Font("Dialog", Font.PLAIN, 16));
        setIconTextGap(20);
    }

    public void setFontSize(int size) {
        setFont(new Font("Dialog", Font.PLAIN, size));
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

}