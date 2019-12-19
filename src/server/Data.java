package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    private String words;
    private String location;
    private ConcurrentHashMap<String,HashMap<String,Integer>> Ricerche = new ConcurrentHashMap<String,HashMap<String,Integer>>(); //hashMap che funziona da "database" in cui salviamo le parole cercate nei vari luoghi
    private ConcurrentHashMap<String,HashMap<String,Integer>> MSW = new ConcurrentHashMap<String,HashMap<String,Integer>>();

    public boolean research(String words, String location) {
        return StoreResearch(location, words);
    }

    public void Store() {

        int count;
        HashMap<String, Integer> Words = new HashMap<String, Integer>();

        try {
            String [] SplitW = this.words.split(" ");

            if(!this.Ricerche.containsKey(location)){
                Ricerche.put(location, Words);
            }
            Words = this.Ricerche.get(location);
            for(String eachWord : SplitW) {
                if(!Words.containsKey(eachWord)) {
                    Words.put(eachWord, 1);
                }
                else {
                    count = Words.get(eachWord);
                    Words.replace(eachWord, count, count+1);
                }
            }
            this.Ricerche.put(this.location, Words);
        }
        catch(Exception e) {
            throw new IllegalArgumentException("Store ERROR");
        }
    }

    public String normalize(String words) {
        if(words == null)
            throw new IllegalArgumentException();
        words = words.replaceAll("[^a-zA-Z]", " ");
        words = words.replaceAll("\\s+", " ");
        words = words.replaceAll("^\\s", "");
        words = words.toLowerCase();
        return words;
    }

    public boolean StoreResearch(String location, String words) {
        this.location = normalize(location);
        this.words = normalize(words);
        if(this.location.isEmpty()||this.words.isEmpty())
            return false;
        Store();
        return true;
    }

    public String Print() {
        String res = "";

        try {

            for (String loc: this.MSW.keySet()){
                String key = loc;
                String value = this.MSW.get(loc).toString();
                value = value.replaceAll("=", ":");
                value = value.replace("{", "[");
                value = value.replace("}", "]");
                res = res.concat(key + ": " + value + ", ");
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
