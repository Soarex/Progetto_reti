import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/*
    Server
    Classe principale che mette a disposizione tutte le funzioni del server
    e inizializza i manager
*/
public class Server extends UnicastRemoteObject implements RegistrationInterface {
    static final long serialVersionUID = 5; 
    public static int PORT = 8080;
    private UserManager userManager;
    private boolean run;

    public Server() throws RemoteException {
        super();
        userManager = UserManager.getInstance();
        userManager.load();
        SfidaManager.getInstance();
        WordManager.getInstance().load();
    }
    
    public void start() {
        System.out.println("Server partito");
        userManager.load();
        run = true;
        try {
            ServerSocket socket = new ServerSocket(PORT);
            Socket clientSocket;
            Thread clientThread;
            while(run) {
                clientSocket = socket.accept();
                clientThread = new ClientThread(clientSocket);
                clientThread.start();
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized public int registraUtente(String nickname, String password) throws RemoteException {
        if(userManager.isRegistred(nickname)) return -1;

        userManager.addUser(new User(nickname, password));
        userManager.store();
        return 0;
    }

    public int login(String nickname, String password, Socket socket) {
        if(!userManager.isRegistred(nickname)) return -1;
        if(userManager.isLogged(nickname)) return -2;
        if(userManager.getUser(nickname).getPassword() != password) return -3;

        userManager.logUserIn(nickname, socket);
        return 0;
    }

    public void logout(String nickname) {
        userManager.logUserOut(nickname);
    }

    public void shutdown() {
        userManager.store();
    }

    private static Server instance;
    public static Server getInstance() {
        if(instance == null) 
            try {
                instance = new Server();    
            } catch(Exception e) {
                e.printStackTrace();
            }
        
        return instance;
    }
}