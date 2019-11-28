package client;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;
import interfacce.ClientInt;
//import server.Server;

public class MyWorker extends SwingWorker<Boolean, Void> {

	private ClientGUI gui;
	private String command;
	private ClientInt client;
	public String location;
	public String searchedW;
	//public Server SER;
	
	public MyWorker(ClientGUI gui, String command, ClientInt client, String location, String searchedW) {
		this.gui = gui;
		this.command = command;
		this.client = client;
		this.location = location;
		this.searchedW = searchedW;
	}
	
	@Override
	protected Boolean doInBackground() throws Exception {
		switch(command) {
		case "research": return client.research(location, searchedW);
		case "print": return client.print();
		case "mostSearchedW": return client.MostSearchedW();
		default: throw new AssertionError();
		}
	}
	
	@Override
	protected void done() {
		boolean result = false;
		try {
			result =  get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			gui.messageField.setText("SERVER NON RAGGIUNGIBILE");
			e.printStackTrace();
			return;
		}
		
		switch(command) {
		case "research": {
			if (!result) gui.messageField.setText("TESTO/LOCATION MANCANTE");
			else{
				gui.messageField.setText("RICERCA EFFETTUATA");
				gui.srcField.setText("");
				gui.locationField.setText("");
			}
			break;
		}
		case "print": {
			if (!result) gui.messageField.setText("IMPOSSIBILE STAMPARE");
			else{
				gui.messageField.setText("STAMPA EFFETTUATA");
			}
			break;
		}
		
		case "mostSearchedW": {
			if (!result) gui.messageField.setText("IMPOSSIBILE STAMPARE LE PAROLE PIU' CERCATE");
			else{
				gui.messageField.setText("STAMPA EFFETTUATA");
			}
			break;
		}
		
		default: throw new AssertionError();
		}
	}
	
}
	