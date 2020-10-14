package esercitazioneProposta1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class RowSwapServer extends Thread{
	
	private final int port;
	private final String fileName;
	
	public RowSwapServer(int port, String fileName) {
		this.port = port;
		this.fileName = fileName;
	}
	
	@Override
	public void run() {
		try {
			DatagramSocket socket = new DatagramSocket(port);
			byte[] data = null;
			DatagramPacket packet = null;
			int clientPort = -1, numLinea1, numLinea2, esito;
			InetAddress clientAddress = null;
			String richiesta = null;
			String[] dueNum = null; 
			
			while(true){
				try {
					data = new byte[256];
					packet = new DatagramPacket(data, data.length);
					System.out.println("RowSwap: " + this.getName() + " in attesa");
					socket.receive(packet);

					clientPort = packet.getPort();
					clientAddress = packet.getAddress();
					packet.setAddress(clientAddress);
					packet.setPort(clientPort);

					System.out.println("RowSwap: " + this.getName() + " ricevuto pacchetto da Client");

					richiesta = Helper.bytesToStringUTF(packet.getData());

					System.out.println("richiesta scambio righe: " + richiesta);

					dueNum = richiesta.split(" ");
					numLinea1 = Integer.parseInt(dueNum[0]);
					numLinea2 = Integer.parseInt(dueNum[1]);

					esito = LineUtility.swapLine(fileName, numLinea1, numLinea2);
					System.out.println("Chiamato metodo swapLine con esito: " + esito + System.lineSeparator() + "RowSwap: " + this.getName() + " invio risposta al Client");
					data = Helper.intToBytes(esito);
					packet.setData(data);
					socket.send(packet);
				}catch(IOException e) {
					e.printStackTrace();
					System.out.println("errore in un pacchetto...continuo");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
}
