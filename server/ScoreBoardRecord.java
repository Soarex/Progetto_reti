/*
    ScoreboardRecord
    Classe per il salvataggio delle informazioni della classifica
*/
public class ScoreBoardRecord implements Comparable<ScoreBoardRecord>{
    public String name;
    public int score;

    public ScoreBoardRecord(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public int compareTo(ScoreBoardRecord r) {
        if(score == r.score) return name.compareTo(r.name);
        if(score < r.score) return 1;
        
        return -1;
    }

    public String toString() {
        return "{" + name + ": " + score + "}";
    }
}