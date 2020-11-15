// Implementazione del Server RMI

import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;

public class Server extends UnicastRemoteObject implements RemOp {

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

		final int REGISTRYPORT = 1099;
		String registryHost = "localhost";
		String serviceName = "ServerCongresso";

		// Registrazione del servizio RMI
		String completeName = "//" + registryHost + ":" + REGISTRYPORT + "/" + serviceName;
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