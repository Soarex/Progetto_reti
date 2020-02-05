import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/*
    ChallengeThread
    Si occupa di ricevere le richieste di sfida su UDP
    Mantiene le richieste ricevute
    Communica la ricezione di una richiesta alla gui tramite eventi
*/
public class ChallengeThread extends Thread {
    private static EventListener sidebarCallback;
    private static EventListener windowCallback;
    private static List<String> requests = new ArrayList<>();

    private int port;
    private boolean run;

    public ChallengeThread(int port) {
        this.port = port;
        run = true;
        setDaemon(true);
    }

    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(new InetSocketAddress(port));
            byte[] buffer = new byte[512];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while(run) {
                socket.receive(packet);
                String challenger = new String(packet.getData(), 0, packet.getLength());
                requests.add(challenger);
                sidebarCallback.action(requests.size() + "");
                if(windowCallback != null)
                    windowCallback.action(challenger);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        run = false;
    }

    public static void addEventListener(EventListener e) {
        windowCallback = e;
    }

    public static void addSidebarListener(EventListener e) {
        sidebarCallback = e;
    }

    public static List<String> getRequests() {
        return requests;
    }

    public static void removeRequest(String request) {
        requests.remove(request);
        sidebarCallback.action(requests.size() + "");
    }
}
