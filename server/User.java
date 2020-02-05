import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/*
    User
    Classe per la gestione delle informazioni relative ad un utente
*/

public class User {
    private String nickname;
    private String password;
    private int score;
    private List<String> friendList;

    public User(JSONObject o) {
        this.friendList = new ArrayList<>();
        deserialize(o);
    }

    public User(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
        this.score = 0;
        this.friendList = new ArrayList<>();
    }
    
    public User(String nickname, String password, int score) {
        this.nickname = nickname;
        this.password = password;
        this.score = score;
        this.friendList = new ArrayList<>();
    }

    public User(String nickname, String password, int score, ArrayList<String> friendList) {
        this.nickname = nickname;
        this.password = password;
        this.score = score;
        this.friendList = friendList;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addToScore(int amount) {
        score += amount;
    }

    public void addFriend(String nickname) {
        friendList.add(nickname);
    }

    public boolean isFriendOf(String nickname) {
        return friendList.contains(nickname);
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public JSONObject serialize() {
        JSONObject o = new JSONObject();
        o.put("Nickname", nickname);
        o.put("Password", password);
        o.put("Score", score);

        JSONArray array = new JSONArray();
        for(String s : friendList)
            array.add(s);

        o.put("FriendList", array);
        return o;
    }

    public void deserialize(JSONObject o) {
        nickname = (String)o.get("Nickname");
        password = (String)o.get("Password");
        score = ((Long)o.get("Score")).intValue();

        JSONArray array = (JSONArray)o.get("FriendList");
        for(Object friend : array)
            friendList.add((String)friend);
    }

    public String toString() {
        return "{Nickname: " + nickname +
                ", Password: " + password +
                ", Score: " + score +
                ", Friends: " + friendList + "}"; 
    }
}