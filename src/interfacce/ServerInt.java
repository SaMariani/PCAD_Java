package interfacce;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInt extends Serializable, Remote {
	public boolean StoreResearch(String location, String words) throws RemoteException;
	public boolean research(String words, String location) throws RemoteException;
	public String Print() throws RemoteException;
	public String normalize(String words) throws RemoteException;
	public void Store() throws RemoteException;
}