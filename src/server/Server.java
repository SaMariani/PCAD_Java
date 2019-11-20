package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import client.ClientGUI;
import interfacce.ServerInt;

public class Server implements ServerInt{
	
	private static final long serialVersionUID = 1L;
	
	ConcurrentHashMap<String,HashMap<String,Integer>> Ricerche; //hashMap per le parole cercate nei vari luoghi
	public String words;
	public String location;
	Random random;
	protected ServerGUI gui;
	public ClientGUI Cgui; 
	
	public Server(ServerGUI x) {
		gui=x;
		Ricerche = new ConcurrentHashMap<String,HashMap<String,Integer>>();
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
	//Da qui in poi è da controllare,vorrei stampare le azioni del client anche nell interfaccia server
	@Override    
	public boolean research(String words, String location) throws RemoteException {
		if(StoreResearch(location, words)) {
			gui.Update("Location "+location+" inserita");
			gui.Update("Stringa "+words+" inserita");
			return true;	
			}
		gui.Update("Location "+location+" non inserita");
		gui.Update("Stringa "+words+" non inserita");
		return false;
	}
	
	//METODI per inserire le parole cercate e la location nell'Hashmap...da rivedere bene
	
	@Override
	public void Store() throws RemoteException
	{
		
		int count;
		HashMap<String, Integer> Words = new HashMap<String, Integer>();

		try {
			String [] SplitW = this.words.split(" ");
			
			if(!this.Ricerche.containsKey(this.location))	{
				Ricerche.put(this.location, Words);
			}
			Words = this.Ricerche.get(this.location);
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
	
	@Override
	public String normalize(String words) throws RemoteException
	{
		if(words == null)
			throw new IllegalArgumentException();
		words = words.replaceAll("[^a-zA-Z]", " ");
		words = words.replaceAll("  ", " ");    
		words = words.toLowerCase();
		return words;
	}

	@Override
	public boolean StoreResearch(String location, String words) throws RemoteException {
		this.location = normalize(location);
		this.words = normalize(words);
		Store();
		return true;
	} 
	
	@Override
	public String Print() throws RemoteException
	{
        String res = "";

		try {
			
			for (String loc: Ricerche.keySet()){
	            String key = loc.toString();
	            String value = Ricerche.get(loc).toString();
	            value = value.toString().replaceAll("=", ":");
	            value = value.toString().replace("{", "[");
	            value = value.toString().replace("}", "]");
				res = res.concat(key + ": " + value + ", ");
				
			}
			return res;
		}
		catch (Exception e) {
			throw new IllegalArgumentException("PrintDatabase");
		}
	}
	
	
}