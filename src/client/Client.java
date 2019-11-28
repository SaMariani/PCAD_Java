package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import interfacce.ClientInt;
import interfacce.ServerInt;

public class Client implements ClientInt{

	private static final long serialVersionUID = 1L;
	private ClientGUI GUI;
	private ClientInt stub;
    	private ServerInt server;
    	private String serverIP;
    	
    	public Client(ClientGUI guiReference , String IP) {
    		try {
    			this.GUI = guiReference;
    			this.serverIP = IP;
    			Registry r = LocateRegistry.getRegistry(serverIP,8080);
    			//Registry r = LocateRegistry.getRegistry(8000); localhost
    			server = (ServerInt) r.lookup("REG");
    			stub = (ClientInt) UnicastRemoteObject.exportObject(this,0);	
    			} catch (RemoteException | NotBoundException e) {
    			System.out.println("Server non collegato");
    			e.printStackTrace();
    		}
    	}
    	
    	@Override
    	public boolean research(String location, String words) throws RemoteException {
    		if (words.equals("") || location.equals("")) return false;
    		return server.research(words, location);
    	}
    	
    	@Override
    	public boolean print() throws RemoteException {
    		return(GUI.Update(server.Print()));
    	}
    	
    	@Override
    	public boolean MostSearchedW() throws RemoteException{
    		return(GUI.Update(server.MostSearchedW()));
    		//return true;
    	}
    	
}