package interfacce;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.Serializable;

public interface ClientInt extends Remote,Serializable {
	public boolean research(String location, String words) throws RemoteException;
	public boolean print() throws RemoteException;
}