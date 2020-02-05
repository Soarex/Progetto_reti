import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/*
    SidePanel
    Si occupa di gestire i tasti di navigazione della barra a lato
*/
public class SidePanel extends JPanel {
    public EventListener mainWindowListener;

    public SidePanel() {
        setBackground(Colors.BACKGROUND_DARK_COLOR);
        //setLayout(new GridLayout(0, 1));

        WideButton friendButton = new WideButton("Amici");
        friendButton.setSelected(true);

        WideButton classificaButton = new WideButton("Classifica");

        WideButton challengeButton = new WideButton("Richieste");

        friendButton.addActionListener((ActionEvent e) -> {
            mainWindowListener.action("amici");
            friendButton.setSelected(true);
            classificaButton.setSelected(false);
            challengeButton.setSelected(false);
        });

        classificaButton.addActionListener((ActionEvent e) -> {
            mainWindowListener.action("classifica");
            friendButton.setSelected(false);
            classificaButton.setSelected(true);
            challengeButton.setSelected(false);
        });

        challengeButton.addActionListener((ActionEvent e) -> {
            mainWindowListener.action("richieste");
            friendButton.setSelected(false);
            classificaButton.setSelected(false);
            challengeButton.setSelected(true);
        });

        add(Box.createRigidArea(new Dimension(50, 20)));
        add(friendButton);
        add(Box.createRigidArea(new Dimension(50, 20)));
        add(classificaButton);
        add(Box.createRigidArea(new Dimension(50, 20)));
        add(challengeButton);

        ChallengeThread.addSidebarListener((String parameter) -> {
            if(parameter.equals("0")) challengeButton.setText("Richieste");
            else challengeButton.setText("Richieste (" + parameter + ")");
        });
    }

    public Dimension getPreferredSize() {
        Dimension parentDimension = getParent().getSize();
        return new Dimension(250, (int)(parentDimension.getHeight()));
    }

    public void addEventListener(EventListener listener) {
        mainWindowListener = listener;
    }
}
