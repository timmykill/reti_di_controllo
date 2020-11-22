// Interfaccia remota

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemOp extends Remote {

	int conta_righe(String nomeFile, int n) throws RemoteException;

	FileLunghezza elimina_riga(String nomeFile, int numeroRiga) throws RemoteException;

}