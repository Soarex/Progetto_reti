import java.util.List;

/*
    Sfida
    Classe per conservare le informazioni relative ad una sfida
*/

public class Sfida {
    public static final int WORD_COUNT = 8;
    public static final int TIME = 30;

    public final Object waitForFriend;
    public final Object endWait;
    public boolean onePlayerDone;
    public boolean accepted;
    public List<String> wordList;
    public List<String> translatedWordList;

    public Sfida() {
        waitForFriend = new Object();
        endWait = new Object();
        onePlayerDone = false;
        accepted = false;

        WordManager wordManager = WordManager.getInstance();
        wordList = wordManager.getRandomWordList(WORD_COUNT);
        translatedWordList = wordManager.translateWordList(wordList);
    }
}