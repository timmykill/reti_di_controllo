package esercitazioneProposta1;

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
			Random r = new Random();
			String richiesta = null;
			int r1, r2, esito, numCicli = r.nextInt(50);
			
			//INIZIO CICLO DI RICHIESTE
			for (int i = 0; i < numCicli; i++) { 
				r1 = r.nextInt(50)+1;
				r2 = r.nextInt(50)+1;
				richiesta = r1 + " " + r2;

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
