package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6969);
        /* still dont know what this is */
        serverSocket.setReuseAddress(true);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            DataInputStream inSocket = new DataInputStream(clientSocket.getInputStream());
            String nomeFile = inSocket.readUTF();
            DataOutputStream outFile = new DataOutputStream(new FileOutputStream(nomeFile + ".bak"));
            FileUtility.trasferisciAByteFileBinario(inSocket, outFile);
            clientSocket.shutdownInput();
            DataOutputStream outSocket = new DataOutputStream(clientSocket.getOutputStream());
            outSocket.writeUTF("yologang");
            clientSocket.shutdownOutput();
            clientSocket.close();
        }
    }
}