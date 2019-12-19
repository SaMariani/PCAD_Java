package server;

import java.util.concurrent.Callable;

public class CallResearch implements Callable<Boolean> {

    private String words;
    private String location;
    private Data data;

    public CallResearch(String words, String location, Data database) {
        this.words = words;
        this.location = location;
        this.data = database;
    }
    @Override
    public Boolean call() {
        return data.research(words,location);
    }
}
