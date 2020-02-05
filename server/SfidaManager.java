import java.util.Hashtable;

/*
    SfidaManager
    Singleton per la gestione delle sfide
*/

public class SfidaManager {
    private Hashtable<String, Sfida> sfidaMap;

    public SfidaManager() {
        sfidaMap = new Hashtable<>();
    }

    synchronized Sfida createSfida(String nickname) {
        Sfida sfida = new Sfida();
        sfidaMap.put(nickname, sfida);
        return sfida;
    }

    synchronized public void removeSfida(String nickname) {
        sfidaMap.remove(nickname);
    }

    synchronized public Sfida getSfida(String nickname) {
        return sfidaMap.get(nickname);
    }

    private static SfidaManager instance;
    public static SfidaManager getInstance() {
        if(instance == null) instance = new SfidaManager();
        return instance;
    }
}