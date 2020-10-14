package esercitazioneProposta1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class Client {
	public static void main(String[] args) {

		//CONTROLLO ARGOMENTI
		try {
			System.out.print(InetAddress.getLocalHost().toString());

			if (args.length != 3) {
				System.out.println("Usage: java Client IPDS PortDS fileName");
				System.exit(1);
			}

			InetAddress addrDiscServer = InetAddress.getByName(args[0]);
			int portDiscServer = Integer.parseInt(args[1]);
			String fileName = args[2];

			if (portDiscServer < 1024 || portDiscServer > 65535) {
				System.out.println("Porta non valida");
				System.exit(2);
			}

			DatagramSocket socket;
			DatagramPacket packet;
			byte[] outBuff = new byte[256];
			byte[] inBuff = new byte[256];

			socket = new DatagramSocket();

			//SCRITTURA IN CODIFICA UTF DELLA RICHIESTA
			outBuff = Helper.stringUTFToBytes(fileName);

			packet = new DatagramPacket(outBuff, outBuff.length, addrDiscServer, portDiscServer);
			//INVIO A DS
			socket.send(packet);

			//RECEIVE
			packet.setData(inBuff);
			socket.receive(packet);

			int portRowServer = Helper.bytesToInt(packet.getData());

			//controllo porta ricevuta da ds

			if (portRowServer < 1024 || portRowServer > 65535)
			    throw new Exception("File non noto porta: " + portRowServer);

			//ds e rs risiedono sulla stessa macchina(= stesso ip), cambio la porta

			packet.setPort(portRowServer);

			//fa n richieste cicliche allo stesso rs e poi termina
			String richiesta = null;
			int num1, num2, esito;
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			String risposta = null;
			String []righe = null;
			//INIZIO CICLO DI RICHIESTE
			
			System.out.println("inserisci le righe da cambiare, termina con EOF");
			while((risposta = stdIn.readLine()) != null){
				righe = risposta.split(" ");
				num1 = Integer.parseInt(righe[0]);
				num2 = Integer.parseInt(righe[1]);
				richiesta = num1 + " " + num2;

				outBuff = Helper.stringUTFToBytes(richiesta);
				packet.setData(outBuff);
				socket.send(packet);

				packet.setData(inBuff);
				socket.receive(packet);
				esito = Helper.bytesToInt(packet.getData());
				System.out.println((esito==0)?"Esito positivo":"Esito negativo");
			}
			socket.close();
			

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
