package esercitazione2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class ServerThread extends Thread{
	
	private final Socket clientSocket;

	public ServerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		//dichiarazione dei due stream
		DataInputStream inSock;
		DataOutputStream outSock;
		String nomeFile, esito;
		FileOutputStream outFile = null;
		long fileDim;

		try {
			inSock = new DataInputStream(this.clientSocket.getInputStream());
			outSock = new DataOutputStream(this.clientSocket.getOutputStream());

			// garbage collection is not resource cleanup (destructors)
			try {
				while (true) {
					nomeFile = inSock.readUTF();
					File file = new File(nomeFile);

					if (file.exists()) {
						System.out.println("il file " + nomeFile + " è già presente");
						esito = "già presente";
						outSock.writeUTF(esito);
					} else {
						esito = "attiva";
						outSock.writeUTF(esito);
						
						fileDim = Integer.parseInt(inSock.readUTF());
						outFile = new FileOutputStream(nomeFile);

						System.out.println("Ricevo il file " + nomeFile + ": \n");
						FileUtility.trasferisci_file_binario(fileDim, inSock, new DataOutputStream(outFile));
						System.out.println("\nRicezione del file " + nomeFile + " terminata\n");
						outFile.close();
					}
				}
			} catch (EOFException ignored) {
				System.out.println("EOF ricevuto \n Terminata connessione con " + clientSocket);
				clientSocket.shutdownInput();
				clientSocket.shutdownOutput();
				clientSocket.close();
				//System.exit(0); //il cliente ha finito le richieste 
			}catch(SocketException e) {
				System.out.println("timeout scattato");
				e.printStackTrace();
				clientSocket.shutdownInput();
				clientSocket.shutdownOutput();
				clientSocket.close();
				System.exit(1);
			}
		} catch (IOException e){
			e.printStackTrace();
			System.exit(1);
		}

}
}
	