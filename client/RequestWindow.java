import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/*
    RequestWindow
    Si occupa di mostrare le richieste di sfida,
    permettendo di accettare o rifiutarle
*/
public class RequestWindow extends JPanel {
    private EventListener mainWindowListener;
    private Client client;

    public RequestWindow() {
        super();
        client = Client.getInstance();
        setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        setLayout(new BorderLayout(50, 80));

        //Pannello del titolo
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

        SimpleLabel label = new SimpleLabel("Richieste di sfida");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFontSize(60);
        topPanel.add(label);

        add(topPanel, BorderLayout.NORTH);

        JPanel centralPanel = new JPanel();//new JPanel(new GridLayout(0, 2, 10, 20));
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));
        centralPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

        //Retrieve delle richieste dal thread di gestione udp
        List<String> requests = ChallengeThread.getRequests();
        if(requests.size() == 0) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);
            SimpleLabel emptyLabel = new SimpleLabel("Nessuna richiesta presente");
            emptyLabel.setFontSize(30);
            panel.add(emptyLabel);
            centralPanel.add(panel);
        } else {
            for (int i = 0; i < requests.size(); i++) {
                String friend = requests.get(i);
                JPanel panel = new JPanel();//new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10)\);
                panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                panel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

                SimpleLabel friendLabel = new SimpleLabel(friend);
                friendLabel.setFontSize(30);
                panel.add(friendLabel);

                panel.add(Box.createRigidArea(new Dimension(50, 50)));
                SimpleLabel messageLabel = new SimpleLabel("");
                messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                messageLabel.setForeground(Color.red);

                SimpleButton accettaButton = new SimpleButton("Accetta");
                //Se sfida accettata, invia la risposta al server e segnala la mainWindow
                accettaButton.addActionListener((ActionEvent e) -> {
                    messageLabel.setForeground(Colors.ACCENT_COLOR);
                    String result = client.accettaRichiesta(friend);

                    if(!result.equals("accettata")) {
                        messageLabel.setForeground(Color.red);
                        messageLabel.setText(result);
                        return;
                    }

                    ChallengeThread.removeRequest(friend);
                    mainWindowListener.action("sfida");
                });
                panel.add(accettaButton);

                panel.add(Box.createRigidArea(new Dimension(50, 50)));

                SimpleButton rifiutaButton = new SimpleButton("Rifiuta");
                //Se riufiuta la richiesta, la rimuove dalla lista
                rifiutaButton.addActionListener((ActionEvent e) -> {
                    messageLabel.setForeground(Colors.ACCENT_COLOR);
                    String result = client.rifiutaRichiesta(friend);
                    if(!result.equals("ok")) {
                        messageLabel.setText(result);
                        return;
                    }

                    ChallengeThread.removeRequest(friend);
                    mainWindowListener.action("refresh");
                });
                panel.add(rifiutaButton);

                panel.add(Box.createRigidArea(new Dimension(50, 50)));

                panel.add(messageLabel);

                centralPanel.add(panel);
                centralPanel.add(Box.createRigidArea(new Dimension(50, 50)));
            }
        }

        add(centralPanel, BorderLayout.CENTER);

        ChallengeThread.addEventListener((String paramenter) -> {
            mainWindowListener.action("refresh");
        });
    }

    public void addEventListener(EventListener listener) {
        mainWindowListener = listener;
    }
}
