package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws IOException {
        /* lettura file */

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("dammi nome file:");
        for (String nomeFile; (nomeFile = stdIn.readLine()) != null; ){
            Socket socket = new Socket(InetAddress.getLocalHost(), 6969);
            DataInputStream inFile = new DataInputStream(new FileInputStream(nomeFile));
            DataOutputStream outSocket = new DataOutputStream(socket.getOutputStream());
            outSocket.writeUTF(nomeFile);
            FileUtility.trasferisciAByteFileBinario(inFile, outSocket);
            inFile.close();
            socket.shutdownOutput();
            DataInputStream inSocket = new DataInputStream(socket.getInputStream());
            String esito = inSocket.readUTF();
            socket.shutdownInput();
            socket.close();
            System.out.println(esito);
        }
    }
}
