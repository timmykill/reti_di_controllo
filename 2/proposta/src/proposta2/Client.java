package proposta2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class Client {

	public static void main(String[] args) {
		
		InetAddress serverAddr = null;
		int serverPort = 0, soglia = 0;
		
		try {
			if(args.length != 3) {
				System.out.println("Usage: Client serverIp serverPort soglia");
				System.exit(1);
			}
			
			serverAddr = InetAddress.getByName(args[0]);
			serverPort = Integer.parseInt(args[1]);
			soglia = Integer.parseInt(args[2]);
		
			if (serverPort < 1024 || serverPort > 65535) {
				System.out.println("Porta non valida");
				System.exit(1);
			}

		}catch(UnknownHostException | NumberFormatException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		//preparazione oggetti usati per comunicare
		FileInputStream fileIn = null;
		Socket socket = null;
		String dirName = null, serverResponse = null, fileName = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		File dir = null;
		Long dimFile;
		
		try {
			//per ogni dir ricevuta
			while((dirName = stdIn.readLine()) != null) {
				dir = new File(dirName);
				if(dir.exists() && dir.isDirectory()) {
					try{
						//connessione
						socket = new Socket(serverAddr, serverPort);
						socket.setSoTimeout(30000);
						inSock = new DataInputStream(socket.getInputStream());
						outSock = new DataOutputStream(socket.getOutputStream());

						//per ogni file nella directory
						for(File elem : dir.listFiles()) {
							if(!elem.isDirectory() && (dimFile = elem.length()) > soglia) {
								try {
									fileName = elem.getName();
									//manda al server il nome del file
									outSock.writeUTF(fileName);						
									serverResponse = inSock.readUTF();
									//se server da l'ok
									if(serverResponse.startsWith("attiva")) {
										fileIn = new FileInputStream(fileName);
										//manda al server lunghezza file (in byte) e file
										outSock.writeLong(dimFile);
										// da fare Utility.TransferFile(new DataInputStream(fileIn), outSock); 
										fileIn.close();
									}
								}catch(IOException | SecurityException e) {
									e.printStackTrace();
								}
							}
						}
						socket.shutdownOutput();
						socket.shutdownInput();
						socket.close();
					}catch(Exception e ) {
					e.printStackTrace();
					continue;
					}
				}else {
					System.out.println(dirName + " non esiste o non è un direttorio");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
