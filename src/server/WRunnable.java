package server;
import java.net.Socket;
import server.Server;
import static java.util.Objects.requireNonNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class WRunnable implements Runnable {
	
	protected Socket clientSocket ;
	protected Server serverRMI;
	
	public WRunnable(Socket clientSocket, Server serverRMI) {
	this.clientSocket = requireNonNull(clientSocket);
	this.serverRMI = requireNonNull(serverRMI) ;
	}

	@Override
	public void run() {
		System.out.println("RUN");
		
		try (PrintWriter output =  new PrintWriter( clientSocket.getOutputStream() , true );
			BufferedReader input = new BufferedReader (new InputStreamReader (clientSocket.getInputStream()));
			Socket client = clientSocket){
				String command = input.readLine();
				switch(command){
					case "research":{
						manageResearch(output, input);
						break;
					}
					case "print":{
						managePrint(output, input);
						break;
					}
					default: {
						output.println("FAIL");
					}
					case "mostSearchedW":{
						manageMostSearchedW(output, input);
						break;
					}
				}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  // Stream for sending data.
			

	}

	private void manageResearch (PrintWriter output , BufferedReader input) throws IOException{
		output.println("OK");
		String location = input.readLine();
		String searchedW = input.readLine();
		boolean result = serverRMI.research(searchedW, location);
		if(result) output.println("OK");
		else output.println("FAIL");
	}

	private void managePrint (PrintWriter output , BufferedReader input) throws IOException{
		output.println("OK");
		output.println(serverRMI.Print());
		System.out.println("stampa: "+serverRMI.Print());
	}

	private void manageMostSearchedW (PrintWriter output , BufferedReader input) throws IOException{
		output.println("OK");
		output.println(serverRMI.MostSearchedW());
		System.out.println("stampa: "+serverRMI.MostSearchedW());
	}
}