import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
    MainWindow
    Classe per la finestra principale
    Si occupa di inizializzare il client e gestisce l'area della finestra
*/
public class MainWindow extends JFrame {
    private Container contentPane;
    private SidePanel sidePanel;
    private Client client;

    public MainWindow() {
        super("Word Quizzle");
        setSize(1280, 720);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                client.close();
            }
        });

        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        client = Client.getInstance();
    }

    public void start() {
        setVisible(true);
        ConnectWindow connectWindow = new ConnectWindow();

        connectWindow.addEventListener((String parameter) -> {
            setLoginWindow();
        });

        add(connectWindow, BorderLayout.CENTER);

        sidePanel = new SidePanel();
        sidePanel.addEventListener((String parameter) -> {
            switch (parameter) {
                case "amici":
                    setFriendWindow();
                    break;

                case "classifica":
                    setScoreboardWindow();
                    break;

                case "richieste":
                    setRequestWindow();
                    break;
            }
        });

        validate();
    }

    //Aggiorna la finestra con un nuovo contenuto
    private void updateContent(JPanel content) {
        contentPane.removeAll();
        add(content, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.WEST);
        revalidate();
        repaint();
        sidePanel.revalidate();
    }

    //Aggiorna la finestra con un nuovo contenuto senza la barra laterale
    private void updateContentNoSidebar(JPanel content) {
        contentPane.removeAll();
        add(content, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void setLoginWindow() {
        LoginWindow lw = new LoginWindow();
        lw.addEventListener((String parameter) -> {
            setFriendWindow();
        });
        updateContentNoSidebar(lw);
    }

    private void setFriendWindow() {
        FriendWindow fw = new FriendWindow();
        fw.addEventListener((String parameter) -> {
            if(parameter.equals("refresh"))
                setFriendWindow();

            if(parameter.equals("sfida"))
                setChallengeWindow();
        });
        updateContent(fw);
    }

    private void setChallengeWindow() {
        ChallengeWindow cw = new ChallengeWindow();
        cw.addEventListener((String parameter) -> {
            if(parameter.equals("exit")) {
                sidePanel = new SidePanel();
                sidePanel.addEventListener((String parameter2) -> {
                    switch (parameter2) {
                        case "amici":
                            setFriendWindow();
                            break;

                        case "classifica":
                            setScoreboardWindow();
                            break;

                        case "richieste":
                            setRequestWindow();
                            break;
                    }
                });

                setFriendWindow();
            }
        });
        updateContentNoSidebar(cw);
    }

    private void setScoreboardWindow() {
        ScoreboardWindow sw = new ScoreboardWindow();
        sw.addEventListener((String parameter) -> {

        });

        updateContent(sw);
    }

    private void setRequestWindow() {
        RequestWindow rw = new RequestWindow();
        rw.addEventListener((String parameter) -> {
            if(parameter.equals("refresh"))
                setRequestWindow();

            if(parameter.equals("sfida"))
                setChallengeWindow();
        });

        updateContent(rw);
    }
}
