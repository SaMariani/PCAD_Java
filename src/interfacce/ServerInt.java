package interfacce;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInt extends Serializable, Remote {
	public boolean research(String words, String location) throws RemoteException;
	public String MostSearchedW() throws RemoteException;
}