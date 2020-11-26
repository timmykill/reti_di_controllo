
import java.rmi.RemoteException;

public interface RegistryRemotoTagClient extends RegistryRemotoClient{
	public String[] cercaTag(String tag)throws RemoteException;//un tag per ottenere nomi logici di server

}
