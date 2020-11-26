import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RMISecurityManager;

@SuppressWarnings("deprecation")
public class ServerCongressoImpl extends UnicastRemoteObject implements ServerCongresso{

	private static final long serialVersionUID = 1L;
	static Programma prog[]; 
	
	// Costruttore
	public ServerCongressoImpl()throws RemoteException {super(); }


	//registrazione
	public int registrazione (int giorno, String sessione, String speaker) throws RemoteException{
		int numSess = -1;
		System.out.println("Server RMI: richiesta registrazione nel giorno "+giorno+
				", sessione "+sessione+" dello speaker "+speaker);
		for(int i=0;i<12;i++) {
			if(sessione.equals("S"+(i+1))) {
				numSess=i;
				break;
			}
		}
		if (numSess == -1 || (numSess<0||numSess>11)) throw new RemoteException();
		
		if (giorno < 1 || giorno > 3) throw new RemoteException();
		
		return prog[giorno-1].registra(numSess,speaker);
	}
	
	//programma
	public Programma programma (int giorno)throws RemoteException{
		System.out.println("Server RMI: programma giorno"+giorno);
		if (giorno < 1 || giorno > 3) throw new RemoteException();
		return prog[giorno-1];
	}
			
			

	public static void main(String[] args) {
		prog = new Programma[3]; // creazione programma
		for (int i = 0; i < 3; i++) prog[i] = new Programma();
		int registryRemotoPort = 1099; // default
		String registryRemotoName = "RegistryRemoto";
		String serviceName = "ServerCongresso";
		String serviceTag="Congresso";
		if (args.length != 1 && args.length != 2) {
			System.out.println("Usage: registryRemotoName [registryRemotoPort]");
			System.exit(1);
		} // Controllo argomenti
		String registryRemotoHost = args[0];
		if (args.length == 2)
		{ try { registryRemotoPort = Integer.parseInt(args[1]); }
		catch (Exception e) {e.printStackTrace();} 
		} // if
		
		
	    // Impostazione del SecurityManager
	    if (System.getSecurityManager() == null) {
	      System.setSecurityManager(new RMISecurityManager());
	    }
		
		// Registrazione servizio presso RegistryRemoto
		String completeRemoteRegistryName = "//"+registryRemotoHost+
				":"+registryRemotoPort+"/"+registryRemotoName;
		try
		{RegistryRemotoTagServer registryRemoto =
		(RegistryRemotoTagServer)Naming.lookup(completeRemoteRegistryName);
		ServerCongressoImpl serverRMI = new ServerCongressoImpl();
		registryRemoto.aggiungi(serviceName, serverRMI);
		if(registryRemoto.associaTag(serviceName, serviceTag))
			System.out.println("il tag "+serviceTag+" è stato aggiunto al servizio di ServerCongresso");
		} catch (Exception e) {e.printStackTrace();}
	}//main

} // ServerCongressoImpl

