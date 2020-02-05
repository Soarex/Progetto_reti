import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/*
    ConnectWindow
    Si occupa di connettersi al server
*/
public class ConnectWindow extends JPanel {
    private Client client;

    private EventListener mainWindowListener;
    private JTextField addressField;
    private SimpleButton connectButton;
    private SimpleLabel errorLabel;

    public ConnectWindow() {
        super();
        setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        setLayout(new BorderLayout(50, 80));

        client = Client.getInstance();

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        formPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        formPanel.setLayout(new GridLayout(0, 1, 50, 30));


        JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        linePanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        SimpleLabel label = new SimpleLabel("Server IP");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        linePanel.add(label);

        addressField = new JTextField(10);
        addressField.setAlignmentX(Component.CENTER_ALIGNMENT);
        addressField.setFont(new Font("Dialog", Font.PLAIN, 24));
        addressField.setHorizontalAlignment(JTextField.CENTER);
        linePanel.add(addressField);

        formPanel.add(linePanel);

        connectButton = new SimpleButton("Connect");
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(connectButton);

        errorLabel = new SimpleLabel("");
        errorLabel.setForeground(Color.red);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(errorLabel);

        connectButton.addActionListener((ActionEvent e) -> {
            client.setParameters(addressField.getText(), 8080);
            if(client.start() != 0) {
                errorLabel.setText("Errore di connessione al server");
                return;
            }

            errorLabel.setText("");
            mainWindowListener.action(null);
        });

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        centerPanel.add(formPanel);
        centerPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        add(centerPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        label = new SimpleLabel("Word Quizzle");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFontSize(60);


        topPanel.add(label);
        add(topPanel, BorderLayout.NORTH);

        validate();
    }

    public void addEventListener(EventListener listener) {
        mainWindowListener = listener;
    }
}
