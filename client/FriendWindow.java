import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/*
    FriendWindow
    Classe per la gestione degli amici
    Permette di vedere gli amici correnti, sfidarli e aggiungerne nuovi
*/
public class FriendWindow extends JPanel {
    private EventListener mainWindowListener;
    private Client client;

    public FriendWindow() {
        super();
        client = Client.getInstance();
        setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        setLayout(new BorderLayout(50, 80));

        //Pannello del titolo
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

        SimpleLabel label = new SimpleLabel("Amici");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFontSize(60);
        topPanel.add(label);

        add(topPanel, BorderLayout.NORTH);

        //Pannello centrale con la lista amici
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));
        centralPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

        //Retrive della lista amici dal server
        List<String> friendList = client.listaAmici();
        if(friendList == null || friendList.size() == 0) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

            SimpleLabel messageLabel;
            if(friendList == null) {
                messageLabel = new SimpleLabel("Errore di connessione");
                messageLabel.setForeground(Color.red);
            } else
                messageLabel = new SimpleLabel("Nessun amico registrato");

            messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(messageLabel);
            centralPanel.add(panel);
        } else {
            for (String friend : friendList) {
                JPanel panel = new JPanel();
                panel.setLayout(new FlowLayout(FlowLayout.CENTER));
                panel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

                SimpleLabel friendLabel = new SimpleLabel(friend);
                friendLabel.setFontSize(30);
                panel.add(friendLabel);

                panel.add(Box.createRigidArea(new Dimension(50, 50)));
                SimpleLabel messageLabel = new SimpleLabel("");
                messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                messageLabel.setForeground(Color.red);

                SimpleButton sfidaButton = new SimpleButton("Sfida");
                sfidaButton.addActionListener((ActionEvent e) -> {
                    messageLabel.setForeground(Colors.ACCENT_COLOR);
                    messageLabel.setText("c");
                    messageLabel.setText("Attesa...");
                    messageLabel.paintImmediately(messageLabel.getVisibleRect());
                    revalidate();
                    String result = client.richiestaSfida(friend);

                    if(!result.equals("accettata")) {
                        messageLabel.setForeground(Color.red);
                        messageLabel.setText(result);
                        return;
                    }

                    mainWindowListener.action("sfida");
                });
                panel.add(sfidaButton);

                panel.add(Box.createRigidArea(new Dimension(50, 50)));

                panel.add(messageLabel);

                centralPanel.add(panel);
                centralPanel.add(Box.createRigidArea(new Dimension(50, 50)));
            }
        }

        //Per lo scroll della lista
        JScrollPane scrollPane = new JScrollPane(centralPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));

        add(scrollPane, BorderLayout.CENTER);

        //Pannello in basso per l'aggiunta di nuovi amici
        JPanel bottomPanel = new JPanel(new GridLayout(2, 4, 40, 20));
        bottomPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        bottomPanel.add(Box.createRigidArea(new Dimension(50, 50)));

        JTextField friendField = new JTextField(25);
        friendField.setAlignmentX(Component.CENTER_ALIGNMENT);
        friendField.setFont(new Font("Dialog", Font.PLAIN, 24));
        friendField.setHorizontalAlignment(JTextField.CENTER);
        bottomPanel.add(friendField);

        SimpleButton addFriendButton = new SimpleButton("Aggiungi amico");
        bottomPanel.add(addFriendButton);
        bottomPanel.add(Box.createRigidArea(new Dimension(50, 50)));
        bottomPanel.add(Box.createRigidArea(new Dimension(50, 20)));

        bottomPanel.add(Box.createRigidArea(new Dimension(50, 20)));
        SimpleLabel errorLabel = new SimpleLabel("");
        errorLabel.setForeground(Color.red);
        bottomPanel.add(errorLabel);

        addFriendButton.addActionListener((ActionEvent e) -> {
            String res = client.aggiungiAmico(friendField.getText());
            if(!res.equals("success")) {
                errorLabel.setText(res);
                return;
            }

            mainWindowListener.action("refresh");
        });

        bottomPanel.add(Box.createRigidArea(new Dimension(50, 20)));

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void addEventListener(EventListener listener) {
        mainWindowListener = listener;
    }
}
