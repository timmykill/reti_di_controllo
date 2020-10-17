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
	
	private Socket clientSocket = null;
	
	public ServerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		//dichiarazione dei due stream
		DataInputStream inSock;
		DataOutputStream outSock;
		String nomeFile=null, esito=null; 
		FileOutputStream outFile=null;
		Long fileDim;
		
		try {
			inSock=new DataInputStream(this.clientSocket.getInputStream());
			outSock=new DataOutputStream(this.clientSocket.getOutputStream());
		
			while((nomeFile=inSock.readUTF())!=null) {//cioè ci sono altri file da copiare
				
				File file = new File(nomeFile);
				
				if(file.exists()) {
					esito="già presente";
					outSock.writeUTF(esito);
				}
				else {
					esito = "attiva trasferimento file";
					outSock.writeUTF(esito);
					fileDim=inSock.readLong();
					
					outFile = new FileOutputStream(nomeFile);
				
					System.out.println("Ricevo il file " + nomeFile + ": \n");
					FileUtility.trasferisci_a_byte_file_binario(fileDim,inSock, new DataOutputStream(outFile));
					System.out.println("\nRicezione del file " + nomeFile + " terminata\n");
					outFile.close();
				}
				
			}//while, se esce sono finiti i file
			
			clientSocket.shutdownInput(); //chiusura socket (downstream)
			clientSocket.shutdownOutput(); //chiusura socket (dupstream)
			System.out.println("\nTerminata connessione con " + clientSocket);
			clientSocket.close();
			
		}catch(EOFException e) {
			e.printStackTrace();
			System.exit(1);
		}catch(SocketException ste){
			System.out.println("Timeout scattato: ");
			ste.printStackTrace();
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out
				.print("\n^D(Unix)/^Z(Win)+invio per uscire, solo invio per continuare: ");
			return;          
		}        
		catch (IOException ioe) {
			System.out
				.println("Problemi nella creazione degli stream di input/output "
						+ "su socket: ");
			ioe.printStackTrace();
			// il server continua l'esecuzione riprendendo dall'inizio del ciclo
			return;
		}
		catch (Exception e) {
			System.out
				.println("Problemi nella creazione degli stream di input/output "
						+ "su socket: ");
			e.printStackTrace();
			return;
		}
		
	}
}
	