import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/*
    MainClass
    Nessun parametro, inizializza server e server RMI
*/
public class MainClass {
    public static void main(String[] args) {
        Server server = Server.getInstance();
        try {
            Registry registry = LocateRegistry.createRegistry(8888);
            registry.rebind("RegistrationServer", server);
        } catch(Exception e) {
            System.err.println("Errore rmi");
            System.exit(-1);
        }
        server.start();
        server.shutdown();
    }
}