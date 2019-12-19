package server;

import java.util.concurrent.Callable;

public class CallPrint implements Callable<String> {

    private Data data;

    public CallPrint(Data database) {
        this.data = database;
    }

    @Override
    public String call() throws Exception {
        return data.MostSearchedW();
    }
}
