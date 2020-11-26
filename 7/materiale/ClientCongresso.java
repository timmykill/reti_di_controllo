import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;

@SuppressWarnings("deprecation")



class ClientCongresso{
	public static boolean sessioneValida(String ses) {
		for(int i=1;i<=12;i++)
			if(ses.equals("S"+i))
				return true;
		return false;
	}
	
	public static void main(String[] args) { // Riportiamo solo inizio main
		int registryRemotoPort = 1099;
		String registryRemotoName = "RegistryRemoto";
		//String serviceName = "ServerCongresso";
		//String serviceTag = "Congresso";
		String serviceName = null;
		String serviceTag = "Congresso";
		String[] services=null;
		BufferedReader stdIn =new BufferedReader(new InputStreamReader(System.in));
		
		//CONTROLLO ARGOMENTI
		if (args.length != 1 && args.length != 2) {
			System.out.println("Sintassi:…");System.exit(1);
			}
		
		String registryRemotoHost = args[0];
		if (args.length == 2){ 
			try {
				registryRemotoPort = Integer.parseInt(args[1]); 
			}catch (Exception e) {e.printStackTrace();}
		}
		
			
		// 	Impostazione del SecurityManager
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
		
		
		// Recupero il riferimento al servizio remoto presso il RegistryRemoto
		try{
			
			String completeRemoteRegistryName = "//" +
				registryRemotoHost + ":" + registryRemotoPort + "/" +
				registryRemotoName;
			RegistryRemotoTagClient registryRemoto = (RegistryRemotoTagClient)
				Naming.lookup(completeRemoteRegistryName);
			services=(String[]) registryRemoto.cercaTag(serviceTag);
			if(services==null || services.length==0|| services[0]==null)
				System.exit(1);
			serviceName=services[0];
			System.out.println("Mi connetto al server: "+serviceName);
			ServerCongresso serverRMI =(ServerCongresso) registryRemoto.cerca(serviceName);
		System.out.println("\nRichieste a EOF");
		System.out.print("Servizio(R=Registrazione, P=Programma): ");
		String service; boolean ok;
		while((service=stdIn.readLine())!=null){
			if (service.equals("R")){ 
				ok=false; int g = 0; // lettura giornata
				System.out.print("Giornata (1-3)? ");
				while (ok!=true){
					g = Integer.parseInt(stdIn.readLine());
					if (g < 1 || g > 3){ 
						System.out.println("Giornata non valida");
						System.out.print("Giornata (1-3)? "); continue;
					} else ok=true;
				} // while interno
				ok=false; String sess = null; // lettura sessione
				System.out.print("Sessione (S1 - S12)? ");
				while (ok!=true){
					sess = stdIn.readLine();
					if ( !sessioneValida(sess)) { 
						continue; }
					else ok=true;
				}
				System.out.print("Speaker? "); // lettura speaker
				String speak = stdIn.readLine();
				// Parametri corretti, invoco il servizio remoto
				if (serverRMI. registrazione (g, sess, speak)==0)
					System.out.println("Registrazione dello speaker "+speak+" nella giornata "+g+", sessione "+sess);
				else System.out.println("Registrazione non effettuata");
			}
		else if (service.equals("P")){ 
			int g = 0; ok=false;
			System.out.print("Giornata (1-3)? ");
			while (ok!=true){
				g = Integer.parseInt(stdIn.readLine());
				if (g < 1 || g > 3){
					System.out.println("Giornata non valida");
					System.out.print("Giornata (1-3)? ");
					continue;
				}else ok=true;
			} // while
			Programma prog = serverRMI.programma(g);
			System.out.println("Programma giornata "+g+"\n");
			prog.stampa();
		} // Operazione P
		else 
			System.out.println("Servizio non disponibile");
		System.out.print("Servizio(R=Registrazione, P=Programma): ");
		} // while
		}catch(Exception e) {e.printStackTrace();}
	}
}