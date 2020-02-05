import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/*
    ChallengeWindow
    Finestra che si occupa della gestione di una sfida
*/
public class ChallengeWindow extends JPanel {
    private EventListener mainWindowListener;
    private Client client;
    private Timer timer = null;
    private boolean timeout;

    public ChallengeWindow() {
        super();
        client = Client.getInstance();
        setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        setLayout(new BorderLayout(50, 80));

        //Pannello del titolo
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

        SimpleLabel label = new SimpleLabel("Sfida");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFontSize(60);
        topPanel.add(label);

        add(topPanel, BorderLayout.NORTH);



        //Pannello centrale con le parole e i risultati
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new GridLayout(6, 1, 50, 10));
        centralPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);

        WideButton text = new WideButton("In attesa di parole");
        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        centralPanel.add(text);

        WideButton giuste = new WideButton("");
        giuste.setAlignmentX(Component.CENTER_ALIGNMENT);
        centralPanel.add(giuste);

        WideButton sbagliate = new WideButton("");
        sbagliate.setAlignmentX(Component.CENTER_ALIGNMENT);
        centralPanel.add(sbagliate);

        WideButton mancate = new WideButton("");
        mancate.setAlignmentX(Component.CENTER_ALIGNMENT);
        centralPanel.add(mancate);

        WideButton punteggio = new WideButton("");
        punteggio.setAlignmentX(Component.CENTER_ALIGNMENT);
        centralPanel.add(punteggio);

        SimpleButton exitButton = new SimpleButton("Esci");
        exitButton.setVisible(false);
        exitButton.addActionListener((ActionEvent e) -> {
            mainWindowListener.action("exit");
        });
        centralPanel.add(exitButton);

        add(centralPanel, BorderLayout.CENTER);


        //Pannello di sotto con la casella per le risposte
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 40, 20));
        bottomPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        bottomPanel.add(Box.createRigidArea(new Dimension(50, 50)));

        JTextField inputField = new JTextField(25);
        inputField.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputField.setFont(new Font("Dialog", Font.PLAIN, 24));
        inputField.setHorizontalAlignment(JTextField.CENTER);

        //Business logic per ogni risposta inserita
        inputField.addActionListener((ActionEvent e) -> {
            String result = client.sendAnswer(inputField.getText().toLowerCase());
            inputField.setText("");
            inputField.paintImmediately(inputField.getVisibleRect());
            revalidate();
            repaint();

            if(result == null) return;
            if(result.equals("error")) {
                text.setForeground(Color.red);
                text.setText("Errore di communicazione");
                exitButton.setVisible(true);
                inputField.setVisible(false);
                return;
            }

            text.setText(result);
            String message = "";

            if(!result.equals("timeout")) {
                message = client.nextMessage();
                if(message == null) return;
                if(message.equals("errore")) {
                    text.setForeground(Color.red);
                    text.setText("Errore di communicazione");
                    exitButton.setVisible(true);
                    revalidate();
                    return;
                }
            }

            //Se la sfida è terminata, mostra i risultati
            if(message.equals("finito") || message.equals("timeout")) {
                timer.stop();
                text.setText("Finito, attesa avversario");

                text.paintImmediately(text.getVisibleRect());
                revalidate();
                repaint();
                List<String> esiti = client.getResults();

                if(message.equals("timeout")) text.setText("Tempo scaduto - " + esiti.get(0));
                else text.setText(esiti.get(0));

                giuste.setText("Giuste: " + esiti.get(1));
                sbagliate.setText("Sbagliate: " + esiti.get(2));
                mancate.setText("Mancate: " + esiti.get(3));
                punteggio.setText("Punti partita: " + esiti.get(4));
                exitButton.setVisible(true);
                inputField.setVisible(false);
                revalidate();
                return;
            }

            text.setText(message);
            revalidate();
        });
        bottomPanel.add(inputField);

        bottomPanel.add(Box.createRigidArea(new Dimension(50, 50)));
        add(bottomPanel, BorderLayout.SOUTH);

        client.startSfida();

        //Timer per terminare la sfida allo scadere del tempo
        timer = new Timer(30000, (ActionEvent e) -> {
            if(timeout) return;
            timeout = true;
            text.setText("Timeout");

            //Riceve il messaggio di timeout
            //Il server invia quel messaggio per le interfacce testuali, ma la gui non lo sfrutta
            client.getTimeoutMessage();

            text.paintImmediately(text.getVisibleRect());
            revalidate();
            repaint();
            List<String> esiti = client.getResults();

            text.setText("Timeout - " + esiti.get(0));

            giuste.setText("Giuste: " + esiti.get(1));
            sbagliate.setText("Sbagliate: " + esiti.get(2));
            mancate.setText("Mancate: " + esiti.get(3));
            punteggio.setText("Punti partita: " + esiti.get(4));
            exitButton.setVisible(true);
            inputField.setVisible(false);
            revalidate();
        });
        timer.start();

        String message = client.nextMessage();
        if(message == null) return;
        if(message.equals("errore")) {
            text.setForeground(Color.red);
            text.setText("Errore di communicazione");
            exitButton.setVisible(true);
            revalidate();
            return;
        }

        text.setText(message);
        revalidate();
    }

    public void addEventListener(EventListener listener) {
        mainWindowListener = listener;
    }
}
