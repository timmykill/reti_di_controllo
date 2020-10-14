package esercitazioneProposta1;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class DiscoveryServer {

    private static boolean checkDuplicati(int[] arr){
        int length = arr.length;
        int[] localArr = Arrays.copyOf(arr, length);
        Arrays.sort(localArr);

        if (length == 0 || length == 1)
            return false;

        for (int i = 1; i < localArr.length; i++){
            if (localArr[i] == localArr[i-1])
                return true;
        }

        return false;
    }

    private static boolean checkDuplicati(String[] arr){
        int length = arr.length;
        String[] localArr = Arrays.copyOf(arr, length);
        Arrays.sort(localArr);

        if (length == 0 || length == 1)
            return false;

        for (int i = 1; i < localArr.length; i++){
            if (localArr[i].equals(localArr[i-1]))
                return true;
        }

        return false;
    }




    public static void main(String []args) {

        int nArgs = args.length;

        if(((nArgs % 2) == 0) || (nArgs < 3)) {
            System.out.println("Usage: java DiscoveryServer portaDS nomeFile1 Porta1 [... nomeFileN PortaN]");
            System.exit(1);
        }

        int nRowSwap = (nArgs-1) / 2; //Numero di server da creare

        String[] arrayFile = new String[nRowSwap];
        int[] arrayPort = new int[nRowSwap];

        try {
            int j = 0;
            for(int i = 1; i < nArgs; i = i + 2) {
                arrayFile[j] = args[i];
                arrayPort[j] = Integer.parseInt(args[i+1]);
                if((arrayPort[j] < 1024) || (arrayPort[j] > 65535)) {
                    System.out.println("Le porte devono essere comprese tra 1024 e 65535");
                    System.exit(1);
                }
                j++;
            }
        } catch(NumberFormatException e) {
            e.printStackTrace();
        }

        if(checkDuplicati(arrayPort)) {
            System.out.println("Le porte devono essere distinte");
            System.exit(1);
        }

      if(checkDuplicati(arrayFile)) {
    	  System.out.println("I file devono essere distinti");
          System.exit(1);
      }
        
      File f=null;
        
      for(int i=0;i<arrayFile.length;i++) {
    	  f=new File(arrayFile[i]);
    	  if(!f.exists() || f.isDirectory()) {
    		  System.out.println(f.toString()+"non esistente o directory");
    		  System.exit(1);
    	  }
      }
      

        for (int i = 0; i < nRowSwap; i++)
            new RowSwapServer(arrayPort[i], arrayFile[i]).start();

        try {
            DatagramSocket socket = new DatagramSocket(Integer.parseInt(args[0]));
            DatagramPacket packet = null;
            String richiesta = null;
            byte[] data = null;
            int rowSwapPort = -1, clientPort = -1;
            InetAddress clientAddress = null;

                while (true) {
                    try {
                    	
                        data = new byte[256];
                    	packet = new DatagramPacket(data, data.length);
                    	socket.receive(packet);

                    	richiesta = Helper.bytesToStringUTF(packet.getData());

                    	//get porta or get -1
                    	rowSwapPort = -1;
                    	for (int i = 0; i < nRowSwap; i++) {
                    		if (richiesta.equals(arrayFile[i])) {
                    			rowSwapPort = arrayPort[i];
                    		}
                    	}

                    	clientPort = packet.getPort();
                    	clientAddress = packet.getAddress();
                    	data = Helper.intToBytes(rowSwapPort);

                    	packet.setPort(clientPort);
                    	packet.setAddress(clientAddress);
                    	packet.setData(data);
                    	socket.send(packet);
                
                    } catch (IOException e){
                    	e.printStackTrace();
                    	System.out.println("problema con un pacchetto, continuo...");
            	}
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
