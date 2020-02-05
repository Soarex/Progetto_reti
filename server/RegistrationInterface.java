import java.rmi.Remote;
import java.rmi.RemoteException;

/*
    RegistrationInterface
    Interfaccia per RMI
*/
public interface RegistrationInterface extends Remote {
    public int registraUtente(String nickname, String password) throws RemoteException;

}