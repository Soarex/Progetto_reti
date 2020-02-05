import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
    UserInstanceData
    Classe per la gestione delle informazioni relative all'istanza corrente del client per l'utente
*/

public class UserInstanceData {
    public Socket socket;
    public int udpPort;
    public boolean isInSfida;
    public Stats currentSfidaStats;
    public List<String> queuedSfide;

    public UserInstanceData(Socket socket) {
        this.socket = socket;
        isInSfida = false;
        currentSfidaStats = new Stats();
        queuedSfide = new ArrayList<>();
        udpPort = socket.getPort() + 1;
    }
}