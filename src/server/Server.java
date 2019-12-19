package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.*;

import client.ClientGUI;
import interfacce.ServerInt;

public class Server implements ServerInt{

	private static final long serialVersionUID = 1L;
	protected ExecutorService pool;
	ConcurrentHashMap<String,HashMap<String,Integer>> Ricerche; //hashMap che funziona da "database" in cui salviamo le parole cercate nei vari luoghi
	ConcurrentHashMap<String,HashMap<String,Integer>> MSW;      //hashmap per le 3 parole più cercate di ogni città M(ost)S(earched)W(ords)
	public String words = "";
	public String location = "";
	Random random;
	protected ServerGUI gui;
	public ClientGUI Cgui;
	private Data data;

	public Server(ServerGUI x) {
		pool = Executors.newFixedThreadPool(5);
		gui=x;
		Ricerche = new ConcurrentHashMap<String,HashMap<String,Integer>>();
		MSW = new ConcurrentHashMap<String,HashMap<String,Integer>>();
		data = new Data();
		try {
			Registry r = null;
			try {
				r = LocateRegistry.createRegistry(8080);
			} catch (RemoteException e) {
				r = LocateRegistry.getRegistry(8080);
			}
			ServerInt stubRequest = (ServerInt) UnicastRemoteObject.exportObject( this,0);
			r.rebind("REG", stubRequest);

			AndroidS androidS = new AndroidS(5005,this);
			(new Thread(){
				public void run(){
					androidS.runWebServer();
				}
			}).start();

			gui.Update("Server REG in ascolto");
			//this.console(r, this, androidS);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean research(String words, String location) throws RemoteException {
		Future<Boolean> f = pool.submit(new CallResearch(words,location,data));
		Boolean res = false;
		try {
			res = f.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public String MostSearchedW() throws RemoteException{
		Future<String> f = pool.submit(new CallPrint(data));
		String res = null;
		try {
			res = f.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return res;
	}


}