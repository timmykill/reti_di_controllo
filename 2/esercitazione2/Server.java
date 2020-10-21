package esercitazione2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
	public static final int DEFAULT_PORT = 1050;

	public static void main(String[] args) throws IOException {

		int port = DEFAULT_PORT;
		
		//CONTROLLO ARGOMENTI
		if (args.length == 1) {
		    try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e){
				System.out.println("Usage: java Server [serverPort>1024]");
				System.out.println(args[0] + " is not a valid port number");
				System.exit(1);
			}
			if (port < 1024 || port > 65535) {
				System.out.println("Usage: java Server [serverPort>1024]");
				System.out.println(args[0] + " is not in range");
				System.exit(1);
			}
		} else if (args.length > 1){
			System.out.println("Usage: java Server [serverPort>1024]");
			System.exit(1);
		}
		
		ServerSocket serverSocket =null;
		
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
			System.out.println("Server: avviato");
			System.out.println("Server: creata la server socket: " + serverSocket);
	    }
	    catch (Exception e) {
	    	System.err
	    		.println("Server: problemi nella creazione della server socket: "
	    				+ e.getMessage());
	    	e.printStackTrace();
	    	System.exit(1);
	    }

		Socket clientSocket = null;

		while (true) {
			System.out.println("Server: in attesa di richieste...\n");

			try {
				clientSocket = serverSocket.accept();
				clientSocket.setSoTimeout(30000);
				System.out.println("Server: connessione accettata: " + clientSocket);
				new ServerThread(clientSocket).start();

			} catch (Exception e) {
				System.err
				.println("Server: problemi nella accettazione della connessione: "
						+ e.getMessage());
				e.printStackTrace();
				continue;
			}
		}

}
}
