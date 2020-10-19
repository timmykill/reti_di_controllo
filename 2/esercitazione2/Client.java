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

public class Client {

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

		//per ogni dir ricevuta
		while ((dirName = stdIn.readLine()) != null) {
			dir = new File(dirName);
			if (!dir.exists() || !dir.isDirectory() || !dir.canRead() || !dir.canExecute()) {
				System.out.println(dirName + " non è una directory o non è accessibile");
				continue;
			}
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
					//manda al server il nome del file
					outSock.writeUTF(fileName);
					//TODO utilizzare qualcosa di più decente delle stringhe:
					// attiva: OK
					// salta: SOMTHING WORNG
					serverResponse = inSock.readUTF();
					//se server da l'ok
					if (serverResponse.startsWith("attiva")) {
						fileIn = new FileInputStream(dir.getName() + File.separator + fileName);
						//manda al server lunghezza file (in byte) e file
						outSock.writeLong(dimFile);
						FileUtility.trasferisci_a_byte_file_binario(
								dimFile,
								new DataInputStream(fileIn),
								outSock
						);
						fileIn.close();
					}
				} catch (IOException | SecurityException e) {
					e.printStackTrace();
				}
			}
			// https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html#close()
			socket.close();
		}
	}
}