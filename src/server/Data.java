package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.Pair;

public class Data {

    private String words;
    private String location;
    private static final int maxWords = 3;
    private ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> Ricerche = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>(); //hashMap che funziona da "database" in cui salviamo le parole cercate nei vari luoghi
    private ConcurrentHashMap<String,HashMap<String,Integer>> MSW = new ConcurrentHashMap<String,HashMap<String,Integer>>();
    private ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> muletto = new ConcurrentHashMap<>();

    public boolean research(String words, String location) {
        this.location = normalize(location);
        this.words = normalize(words);
        if(this.location.isEmpty() || this.words.isEmpty())
            return false;
        Store();
        return true;
    }

    public void Store() {
        ConcurrentHashMap<String, Integer> mapWords = new ConcurrentHashMap<String, Integer>();
        if(Ricerche.putIfAbsent(location, mapWords)==null)
            storeWords(mapWords);
        else
            //mapWords = Ricerche.get(location);
            storeWords(Ricerche.get(location));
    }

    public void updateMSW(String eachWord, int valueI) {
        System.out.println("word to insert: "+eachWord+" value: "+valueI);
        ConcurrentHashMap<String, Integer> MSWmapWords = new ConcurrentHashMap<String, Integer>();
        if(muletto.putIfAbsent(location, MSWmapWords)==null) {
            System.out.println("AGGIUNGO hash to location: " + MSWmapWords);
            updateWords(MSWmapWords, eachWord, valueI);
        }
        else {
            //mapWords = Ricerche.get(location);
            System.out.println("PRIMA else: "+muletto.get(location));
            updateWords(muletto.get(location), eachWord, valueI);
            System.out.println("DOPO else: "+muletto.get(location));
        }
    }

    private String findMin(ConcurrentHashMap<String, Integer> MSWmapWords){
        int min=0;
        String wordMin=null;
        boolean flag=true;
        for (String W: MSWmapWords.keySet()){
            if(flag) {
                wordMin=W;
                min=MSWmapWords.get(W);
                flag=false;
            }else{
                int value = MSWmapWords.get(W);
                if(value < min) {
                    min = value;
                    wordMin=W;
                }
                System.out.println("DOPO CICLO: "+MSWmapWords);
            }
        }
        return wordMin;
    }

    public synchronized void updateWords(ConcurrentHashMap<String, Integer> MSWmapWords, String eachWord, int valueI) {
        System.out.println("INIT: "+MSWmapWords);
        if(MSWmapWords.size()<maxWords)
            if(MSWmapWords.putIfAbsent(eachWord, valueI)==null){
                //++numOfWords;
                System.out.println("+   +   +   + parola non presente, la aggiungo alla lista parole "+MSWmapWords);
                return;}
        if(MSWmapWords.replace(eachWord, valueI)!=null)
            return;
        System.out.println("PRIMA CICLO: "+MSWmapWords);
        String wordMin=findMin(MSWmapWords);
        int value = MSWmapWords.get(wordMin);
        if(value < valueI) {
            System.out.println("RIMUOVO E AGGIUNGO: "+eachWord+" value: "+valueI);
            MSWmapWords.remove(wordMin, value);
            MSWmapWords.put(eachWord, valueI);
        }
        System.out.println("DOPO CICLO: "+MSWmapWords);
        System.out.println("AIUTOOOOOO"+MSWmapWords+Ricerche.get(location));
    }

    public void storeWords(ConcurrentHashMap<String, Integer> mapWords) {            Ricerche.get(location).put("alee",66);

        String [] SplitW = words.split(" ");
        for(String eachWord : SplitW) {
            if(mapWords.putIfAbsent(eachWord, 1)!=null) {
                incrementValue(mapWords, eachWord);
            }
            updateMSW(eachWord, mapWords.get(eachWord));
        }
    }

    public synchronized void incrementValue(ConcurrentHashMap<String, Integer> mapWords, String key) {
        int count = mapWords.get(key);
        mapWords.replace(key, count+1);
    }

    public String normalize(String words) {
        if(words == null)
            throw new IllegalArgumentException();
        words = words.replaceAll("[^a-zA-Z]", " ");
        words = words.replaceAll("\\s+", " ");
        words = words.replace("^\\s", "");
        words = words.toLowerCase();
        return words;
    }

    public synchronized String Print() {
        String res = "";
        for (String loc: muletto.keySet()){ //
            String value = muletto.get(loc).toString();
            value = value.replaceAll("=", ":");
            value = value.replace("{", "[");
            value = value.replace("}", "]");
            res = res.concat(loc + ": " + value + ", ");
        }
        //gui.Update("Parole più frequenti stampate");
        return res;
    }

    public synchronized String MostSearchedW() {
        return Print();
    }
}
