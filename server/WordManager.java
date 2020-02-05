import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
    WordManager
    Gestisce le parole italiane e le traduzioni
*/

public class WordManager {
    public static final String WORD_FILE_LOCATION = "data/words.txt";
    private List<String> wordList;
    private Random randomGenerator;

    public WordManager() {
        wordList = new ArrayList<>();
        randomGenerator = new Random(System.currentTimeMillis());
    }

    public void load() {
        try(BufferedReader reader = new BufferedReader(new FileReader(WORD_FILE_LOCATION))) {
            String word;
            while((word = reader.readLine()) != null)
                wordList.add(word);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getRandomWord() {
        return wordList.get(randomGenerator.nextInt(wordList.size()));
    }

    public List<String> getRandomWordList(int size) {
        if(size > wordList.size()) return null;
        List<String> list = new ArrayList<>(size);
        SortedSet<Integer> generatedNumbers = new TreeSet<>();

        for(int i = 0; i < size; i++) {
            int number = randomGenerator.nextInt(wordList.size());
            while (generatedNumbers.contains(number)) 
                number = (number + 1) % wordList.size();
            
                generatedNumbers.add(number);
        }

        for(int i : generatedNumbers)
            list.add(wordList.get(i));

        return list;
    }

    public String translateWord(String word) {
        String json = Utils.httpRequest("https://api.mymemory.translated.net/", "get?q=" + word + "&langpair=it|en");
        JSONParser parser = new JSONParser();

        try {
            JSONObject o = (JSONObject)parser.parse(json);
            long responseStatus = (long)o.get("responseStatus");
            if(responseStatus != 200)
                return null;

            JSONObject responseData = (JSONObject)o.get("responseData");
            String translatedWord = (String)responseData.get("translatedText");
            return translatedWord;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> translateWordList(List<String> words) {
        List<String> translatedWords = new ArrayList<>(words.size());
        for(String w : words) {
            String s = translateWord(w);
            if(s == null) return null;
            translatedWords.add(s);
        }

        return translatedWords;
    }

    private static WordManager instance;
    public static WordManager getInstance() {
        if(instance == null) instance = new WordManager();
        return instance;
    }
}