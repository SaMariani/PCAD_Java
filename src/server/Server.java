package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import client.ClientGUI;
import interfacce.ServerInt;

public class Server implements ServerInt{
	
	private static final long serialVersionUID = 1L;
	
	ConcurrentHashMap<String,HashMap<String,Integer>> Ricerche; //hashMap che funziona da "database" in cui salviamo le parole cercate nei vari luoghi
	ConcurrentHashMap<String,HashMap<String,Integer>> MSW;      //hashmap per le 3 parole più cercate di ogni città M(ost)S(earched)W(ords)
	public String words = "";
	public String location = "";
	Random random;
	protected ServerGUI gui;
	public ClientGUI Cgui; 
	
	public Server(ServerGUI x) {
		gui=x;
		Ricerche = new ConcurrentHashMap<String,HashMap<String,Integer>>();
		MSW = new ConcurrentHashMap<String,HashMap<String,Integer>>();
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
		if(StoreResearch(location, words)) {
			gui.Update("Location "+location+" inserita");
			gui.Update("Stringa "+words+" inserita");
			return true;	
			}
		gui.Update("Location "+location+" non inserita");
		gui.Update("Stringa "+words+" non inserita");
		return false;
	}
	
	@Override
	public void Store() throws RemoteException{
	
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
			
        	for (String loc: this.MSW.keySet()){
	            String key = loc.toString();
	            String value = this.MSW.get(loc).toString();
	            value = value.toString().replaceAll("=", ":");
	            value = value.toString().replace("{", "[");
	            value = value.toString().replace("}", "]");
				res = res.concat(key + ": " + value + ", ");
			}
        	gui.Update("Parole più frequenti stampate");
			return res;
        }
		catch (Exception e) {
			throw new IllegalArgumentException("Print");
		}
	}
	
	
	
	@Override
	public String MostSearchedW() throws RemoteException{
		
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
						if(map.get(s) == i) {
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