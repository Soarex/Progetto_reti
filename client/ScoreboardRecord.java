/*
    ScoreboardRecord
    Classe che contiene i valori di un record della classifica
*/
public class ScoreboardRecord implements Comparable<ScoreboardRecord>{
    public String name;
    public int score;

    public ScoreboardRecord(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public int compareTo(ScoreboardRecord r) {
        if(score == r.score) return 0;
        if(score > r.score) return 1;
        
        return -1;
    }

    public String toString() {
        return "{" + name + ": " + score + "}";
    }
}