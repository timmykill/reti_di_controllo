// Implementazione del Server RMI

import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;

public class Server extends UnicastRemoteObject implements RemOp {

	private static final long serialVersionUID = -2740891578873754123L;
	static Gestore gestore;

	public Server() throws RemoteException {
		super();
	}

	@Override
	public int conta_righe(String nomeFile, int n) throws RemoteException {
		System.out.println("Server RMI: richiesta conta_righe con parametri");
		System.out.println("nomeFile   = " + nomeFile);
		System.out.println("n = " + n);

		int result;
		try {
			result = gestore.conta(nomeFile, n);
		} catch (IOException e) {
			throw new RemoteException();
		}
		return result;

	}

	@Override
	public FileLunghezza elimina_riga(String nomeFile, int numeroRiga) throws RemoteException {
		System.out.println("Server RMI: richiesta elimina_riga con parametri");
		System.out.println("nomeFile   = " + nomeFile);
		System.out.println("numeroRiga = " + numeroRiga);

		FileLunghezza result;
		try {
			result = gestore.elimina_riga(nomeFile, numeroRiga);
		} catch (IOException e) {
			throw new RemoteException();
		}
		return result;
	}

	public static void main(String[] args) {
		
		gestore = new Gestore();
		int registryPort = 0;
		final int REGISTRYPORT = 1099;
		String registryHost = "localhost";
		String serviceName = "Server";
		
		if(args.length == 1) {
			registryPort = Integer.parseInt(args[0]);
		}else if(args.length == 0) {
			registryPort = REGISTRYPORT;
		}else {
			System.out.println("Sintassi: Server [registryPort]");
			System.exit(1);
		}

		// Registrazione del servizio RMI
		String completeName = "//" + registryHost + ":" + registryPort + "/" + serviceName;
		try {
			Server serverRMI = new Server();
			Naming.rebind(completeName, serverRMI);
			System.out.println("Server RMI: Servizio \"" + serviceName + "\" registrato");
		} catch (Exception e) {
			System.err.println("Server RMI \"" + serviceName + "\": " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}