package server;

import java.util.concurrent.ConcurrentHashMap;

public class Data {

    private String words;
    private String location;
    private static final int maxWords = 3;
    private ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> Ricerche = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>(); //hashMap che funziona da "database" in cui salviamo le parole cercate nei vari luoghi
    private ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> MSW = new ConcurrentHashMap<>();

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

    public void storeWords(ConcurrentHashMap<String, Integer> mapWords) {
        String [] SplitW = words.split(" ");
        for(String eachWord : SplitW) {
            if(mapWords.putIfAbsent(eachWord, 1)!=null) {
                incrementValue(mapWords, eachWord);
            }
            updateMSW(eachWord, mapWords.get(eachWord));
        }
    }

    public void updateMSW(String eachWord, int valueI) {
        ConcurrentHashMap<String, Integer> MSWmapWords = new ConcurrentHashMap<String, Integer>();
        if(MSW.putIfAbsent(location, MSWmapWords)==null)
            updateWords(MSWmapWords, eachWord, valueI);
        else
            updateWords(MSW.get(location), eachWord, valueI);
    }

    public synchronized void updateWords(ConcurrentHashMap<String, Integer> MSWmapWords, String eachWord, int valueI) {
        if(MSWmapWords.size()<maxWords)
            if(MSWmapWords.putIfAbsent(eachWord, valueI)==null)
                return;
        if(MSWmapWords.replace(eachWord, valueI)!=null)
            return;
        String wordMin=findMin(MSWmapWords);
        int value = MSWmapWords.get(wordMin);
        if(value < valueI) {
            MSWmapWords.remove(wordMin, value);
            MSWmapWords.put(eachWord, valueI);
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
            }
        }
        return wordMin;
    }

    private synchronized void incrementValue(ConcurrentHashMap<String, Integer> mapWords, String key) {
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

    public synchronized String MostSearchedW() {
        String res = "";
        for (String loc: MSW.keySet()){ //
            String value = MSW.get(loc).toString();
            value = value.replaceAll("=", ":");
            value = value.replace("{", "[");
            value = value.replace("}", "]");
            res = res.concat(loc + ": " + value + ", ");
        }
        //gui.Update("Parole più frequenti stampate");
        return res;
    }
}
