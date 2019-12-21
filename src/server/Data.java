package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    private String words;
    private String location;
    private ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>> Ricerche = new ConcurrentHashMap<String,ConcurrentHashMap<String,Integer>>(); //hashMap che funziona da "database" in cui salviamo le parole cercate nei vari luoghi
    private ConcurrentHashMap<String,HashMap<String,Integer>> MSW = new ConcurrentHashMap<String,HashMap<String,Integer>>();

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
            if(mapWords.putIfAbsent(eachWord, 1)!=null)
                incrementValue(mapWords, eachWord);
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

    public String Print() {
        String res = "";

        try {

            for (String loc: this.MSW.keySet()){
                String value = this.MSW.get(loc).toString();
                value = value.replaceAll("=", ":");
                value = value.replace("{", "[");
                value = value.replace("}", "]");
                res = res.concat(loc + ": " + value + ", ");
            }
            //gui.Update("Parole più frequenti stampate");
            return res;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Print");
        }
    }

    public String MostSearchedW() {

        HashMap<String, Integer> tmp = new HashMap<String, Integer>();

        try	{
            for(String citta : Ricerche.keySet()){

                HashMap<String, Integer> map = new HashMap<String, Integer>(Ricerche.get(citta));

                List<Integer> occurrence = new ArrayList<Integer>();

                for(String w : map.keySet()) {
                    occurrence.add(map.get(w));
                }

                Collections.sort(occurrence, Collections.reverseOrder());

                for(Integer i : occurrence) {
                    for(String s : map.keySet()) {
                        if(map.get(s).equals(i)) {
                            tmp.put(s,i);
                            map.remove(s,i);
                            break;
                        }
                    }
                    if(tmp.size() == 3)
                        break;
                }
                HashMap<String, Integer> nuova  = new HashMap<String, Integer>(tmp);
                MSW.put(citta, nuova);
                tmp.clear();
                map.clear();
            }
            return Print();

        }

        catch(Exception e)	{
            throw new IllegalArgumentException("MostSEarchedW");
        }

    }
}
