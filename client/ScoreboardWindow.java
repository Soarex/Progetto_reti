import javax.swing.*;
import java.awt.*;
import java.util.List;

/*
    ScoreboardWindow
    Si occupa di richiedere e mostrare la classifica
*/
public class ScoreboardWindow extends JPanel {
    private Client client;
    private EventListener mainWindowListener;

    public ScoreboardWindow() {
        client = Client.getInstance();
        setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        setLayout(new BorderLayout(50, 80));

        //Pannello del titolo
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

        SimpleLabel label = new SimpleLabel("Classifica");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFontSize(60);
        topPanel.add(label);
        add(topPanel, BorderLayout.NORTH);


        //Pannello centrale con la classifica
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));
        centralPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

        //Retrieve della classifica dal server
        List<ScoreboardRecord> scoreboard = client.classifica();
        if(scoreboard == null || scoreboard.isEmpty()) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

            SimpleLabel messageLabel;
            if(scoreboard == null) {
                messageLabel = new SimpleLabel("Errore di communicazione");
                messageLabel.setForeground(Color.red);
            } else
                messageLabel = new SimpleLabel("Classifica vuota");

            messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(messageLabel);
            centralPanel.add(panel);
        } else {
            for (ScoreboardRecord record : scoreboard) {
                JPanel panel = new JPanel();
                //panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                panel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

                WideButton nameLabel = new WideButton(record.name + "    " + record.score);
                if(record.name.equals(client.getName()))
                    nameLabel.setSelected(true);
                panel.add(nameLabel);

                centralPanel.add(panel);
            }
        }

        JScrollPane scrollPane = new JScrollPane(centralPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addEventListener(EventListener listener) {
        mainWindowListener = listener;
    }
}
