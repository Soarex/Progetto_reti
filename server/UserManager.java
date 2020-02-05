import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.Socket;
import java.util.Hashtable;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/*
    UserManager
    Singleton per la gestione degli utenti
    Si occupa anche di preservare le informazioni su file
*/

public class UserManager {
    public static final String DATA_FILE_LOCATION = "data/data.json";

    private Hashtable<String, User> userMap;
    private Hashtable<String, UserInstanceData> loggedUsers;
    
    public UserManager() {
        userMap = new Hashtable<>();
        loggedUsers = new Hashtable<>();
    }

    synchronized public void addUser(User user) {
        userMap.put(user.getNickname(), user);
    }

    synchronized public void removeUser(String nickname) {
        userMap.remove(nickname);
    }

    synchronized public User getUser(String nickname) {
        return userMap.get(nickname);
    }

    synchronized public boolean isRegistred(String nickname) {
        return getUser(nickname) != null;
    }

    synchronized public void logUserIn(String nickname, Socket socket) {
        loggedUsers.put(nickname, new UserInstanceData(socket));
    }

    synchronized public void logUserOut(String nickname) {
        loggedUsers.remove(nickname);
    }

    synchronized public boolean isLogged(String nickname) {
        return loggedUsers.containsKey(nickname);
    }

    synchronized public String getClassifica(String nickname) {
        JSONArray array = new JSONArray();
        
        User user = userMap.get(nickname);
        if(user == null) return null;
        
        SortedSet<ScoreBoardRecord> set = new TreeSet<>();
        set.add(new ScoreBoardRecord(user.getNickname(), user.getScore()));
        
        for(String s : user.getFriendList()) {
            User u = userMap.get(s);
            set.add(new ScoreBoardRecord(u.getNickname(), u.getScore()));
        }
        
        for(ScoreBoardRecord r : set) {
            JSONObject o = new JSONObject();
            o.put("Name", r.name);
            o.put("Score", r.score);
            array.add(o);
        }

        return array.toJSONString();
    }

    synchronized public UserInstanceData getInstanceData(String nickname) {
        return loggedUsers.get(nickname);
    }

    public void load() {
        JSONParser parser = new JSONParser();

        try {
            JSONArray array = (JSONArray)parser.parse(new BufferedReader(new FileReader(DATA_FILE_LOCATION)));

            for(Object o : array) {
                User u = new User((JSONObject) o);
                userMap.put(u.getNickname(), u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void store() {
        JSONArray array = new JSONArray();
        for(User u : userMap.values())
            array.add(u.serialize());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE_LOCATION))){
            writer.write(array.toJSONString());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static UserManager instance;
    public static UserManager getInstance() {
        if(instance == null) instance = new UserManager();
        return instance;
    }
}