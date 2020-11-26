import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@SuppressWarnings("deprecation")
public class ServerGenericoImpl extends UnicastRemoteObject implements ServerGenerico{
	
	private static final long serialVersionUID = 1L;
	
	// Costruttore
	public ServerGenericoImpl()throws RemoteException {super(); }


	public static void main(String[] args) {
		int registryRemotoPort = 1099; // default
		String registryRemotoName = "RegistryRemoto";
		String serviceName = null;
		String serviceTag=null;
		if (args.length != 3 && args.length != 4) {
			System.out.println("Usage: registryRemotoHost [registryRemotoPort] serviceName serviceTag");
			System.exit(1);
		} // Controllo argomenti
		String registryRemotoHost = args[0];
		if (args.length == 4){ 
			try { 
				registryRemotoPort = Integer.parseInt(args[1]); 
				serviceName = args[2];
				serviceTag = args[3];
			}catch (Exception e) {e.printStackTrace();} 
		} else {
			serviceName = args[1];
			serviceTag = args[2];
		}
		
		if (System.getSecurityManager() == null) {
		      System.setSecurityManager(new RMISecurityManager());
		    }
		
		// Registrazione servizio presso RegistryRemoto
		String completeRemoteRegistryName = "//"+registryRemotoHost+
				":"+registryRemotoPort+"/"+registryRemotoName;
		try
		{RegistryRemotoTagServer registryRemoto =
		(RegistryRemotoTagServer)Naming.lookup(completeRemoteRegistryName);
		ServerGenericoImpl serverRMI = new ServerGenericoImpl();
		registryRemoto.aggiungi(serviceName, serverRMI);
		if(registryRemoto.associaTag(serviceName, serviceTag))
			System.out.println("il tag "+serviceTag+" è stato aggiunto al servizio di ServerCongresso");
		} catch (Exception e) {e.printStackTrace();}
	}//main

}
