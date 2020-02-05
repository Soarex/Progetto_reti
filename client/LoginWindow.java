import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/*
    LoginWindow
    Si occupa di effettuare login o registrazione
*/
public class LoginWindow extends JPanel {
    private Client client;

    private EventListener mainWindowListener;
    private JTextField usernameField;
    private JTextField passwordField;
    private SimpleButton loginButton;
    private SimpleButton registrationButton;
    private SimpleLabel errorLabel;

    public LoginWindow() {
        super();
        setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        setLayout(new BorderLayout(50, 80));

        client = Client.getInstance();

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Colors.BACKGROUND_LIGHT_COLOR);
        formPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        formPanel.setLayout(new GridLayout(0, 2, 50, 30));

        SimpleLabel label = new SimpleLabel("Username");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(label);

        usernameField = new JTextField(10);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 24));
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        formPanel.add(usernameField);

        label = new SimpleLabel("Password");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(label);

        passwordField = new JPasswordField(10);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 24));
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        formPanel.add(passwordField);


        loginButton = new SimpleButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(loginButton);

        registrationButton =  new SimpleButton("Register");
        registrationButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registrationButton.addActionListener((ActionEvent e) -> {
            String res = client.registra(usernameField.getText(), passwordField.getText());
            if(res.equals("ok")) {
                res = client.login(usernameField.getText(), passwordField.getText());
                if(!res.equals("Success")) {
                    errorLabel.setText(res);
                    return;
                }
                mainWindowListener.action(null);
                return;
            }
            errorLabel.setText(res);
        });
        formPanel.add(registrationButton);

        errorLabel = new SimpleLabel("");
        errorLabel.setForeground(Color.red);
        formPanel.add(errorLabel);

        loginButton.addActionListener((ActionEvent e) -> {
            String res = client.login(usernameField.getText(), passwordField.getText());
            if(!res.equals("Success")) {
                errorLabel.setText(res);
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
        label = new SimpleLabel("Login");
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
