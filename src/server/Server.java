package server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;



import client.ClientGUI;
import interfacce.ServerInt;

public class Server implements ServerInt{
	
	private static final long serialVersionUID = 1L;
	
	ConcurrentHashMap<String,HashMap<String,Integer>> Ricerche; //hashMap per le parole cercate nei vari luoghi
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
		System.out.println(Ricerche + " 1");
		int count;
		HashMap<String, Integer> Words = new HashMap<String, Integer>();
		System.out.println(Ricerche + " 2");
		try {
			String [] SplitW = this.words.split(" ");
			System.out.println(Ricerche + " 3");
			if(!this.Ricerche.containsKey(location))	{
				System.out.println(Ricerche + " 4");
				Ricerche.put(location, Words);
			}
			System.out.println(Ricerche + " 5");
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
			System.out.println(Ricerche + " 6");
			this.Ricerche.put(this.location, Words);
			System.out.println(Ricerche + " 7");
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
			return res;
        }
		catch (Exception e) {
			throw new IllegalArgumentException("Print");
		}
	}
	
	
	
	@Override
	public String MostSearchedW() throws RemoteException{
		System.out.println(Ricerche);
		MSW.clear();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		HashMap<String, Integer> tmp = new HashMap<String, Integer>();
		//int max;
		//String maxKey;
		
		try	{
			for(String citta : Ricerche.keySet())	{
				//maxKey = "";
				
				map = Ricerche.get(citta);		
				
				System.out.println(Ricerche);
				
				List<Integer> occurrence = new ArrayList<Integer>();;
				
				System.out.println(2);
				
				for(String w : map.keySet()) {
					System.out.println(2.1);
					occurrence.add(map.get(w));
				}
				
				System.out.println(3);
				
				Collections.sort(occurrence, Collections.reverseOrder());
				
				System.out.println(occurrence);
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
				this.MSW.put(citta, nuova);
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