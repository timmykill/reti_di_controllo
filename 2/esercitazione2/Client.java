package esercitazione2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class Client{
	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			System.out.println("Usage: Client serverIp serverPort soglia");
			System.exit(1);
		}

		InetAddress serverAddr = InetAddress.getByName(args[0]);
		int serverPort = Integer.parseInt(args[1]);
		int soglia = Integer.parseInt(args[2]);

		if (serverPort < 1024 || serverPort > 65535) {
			System.out.println("Porta non valida");
			System.exit(1);
		}

		//preparazione oggetti usati per comunicare
		FileInputStream fileIn = null;
		Socket socket = null;
		String dirName = null, serverResponse = null, fileName = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		File dir = null;
		long dimFile;

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Inserire una directory:");
		
		while ((dirName = stdIn.readLine()) != null &&( !(dir = new File(dirName)).exists() || 
				!dir.isDirectory() || !dir.canRead() || !dir.canExecute())) {
			System.out.println("Directory non valida");
		}
		
		if(dirName==null) 
			System.exit(1);
		
		try {
		//connessione
		socket = new Socket(serverAddr, serverPort);
		socket.setSoTimeout(30000);
		inSock = new DataInputStream(socket.getInputStream());
		outSock = new DataOutputStream(socket.getOutputStream());

		//per ogni file nella directory
		for (File elem : dir.listFiles()) {
			dimFile = elem.length();

			if (elem.isDirectory() || dimFile <= soglia) {
				continue;
			}
				try {
					fileName = elem.getName();
					
					outSock.writeUTF(fileName);//manda al server il nome del file
					
					serverResponse=inSock.readUTF();//lettura risposta server
					
					if (serverResponse.startsWith("attiva")) {
						System.out.println("invio "+fileName+" al server");
						fileIn = new FileInputStream(dir.getName() + File.separator + fileName);
						
						outSock.writeUTF(dimFile+"");
						//outSock.writeLong(dimFile);//manda al server lunghezza file (in byte) e file
						
						FileUtility.trasferisci_a_byte_file_binario(
								dimFile,
								new DataInputStream(fileIn),
								outSock
						);
						
						System.out.println("Trasferimento di "+fileName+" terminato con successo");
						
						fileIn.close();
					}else {
						System.out.println(fileName+" è già presente");
					}
				} catch (IOException | SecurityException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Connessione terminata.");
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
			System.exit(0);
		}catch(SocketException e) {
			System.out.println("timeout scattato");
			socket.shutdownInput();
			socket.shutdownOutput();
			socket.close();
		}catch(IOException e) {
			e.printStackTrace();
			socket.close();//errore nella creazione della socket
		}
		
		}
}