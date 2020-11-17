// Implementazione del Client RMI

import java.rmi.*;
import java.io.*;

class Client {

	public static void main(String[] args) {

		final int REGISTRYPORT = 1099;
		int registryPort = 0;
		String registryHost = null;
		String serviceName = "Server";
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		// Controllo dei parametri della riga di comando
		if(args.length == 2) {
			registryPort = Integer.parseInt(args[1]);
		}else if(args.length == 1) {
			registryPort = REGISTRYPORT;
		}else {
			System.out.println("Sintassi: Client NomeHost [registryPort]");
			System.exit(1);
		}
		registryHost = args[0];

		try {
			String completeName = "//" + registryHost + ":" + registryPort + "/" + serviceName;
			RemOp serverRMI = (RemOp) Naming.lookup(completeName);
			System.out.println("ClientRMI: Servizio \"" + serviceName + "\" connesso");

			System.out.println("\nRichieste di servizio fino a fine file");

			String service;
			System.out.print("Servizio (C=conta_righe, E=elimina_riga): ");

			while ((service = stdIn.readLine()) != null) {

				if (service.equalsIgnoreCase("C")) {

					System.out.print("Nome file: ");
					String file = stdIn.readLine();

					boolean ok = false;

					int g = 0;
					System.out.print("Numero parole: ");
					while (ok != true) {
						g = Integer.parseInt(stdIn.readLine());
						if (g < 0) {
							System.out.println("Numero minore di 0");
							System.out.print("Numero parole: ");
							continue;
						} else
							ok = true;
					}

					int r = serverRMI.conta_righe(file, g);
					System.out.println("Il numero di righe con piu di " + g + "parole e: " + r);
				}

				else if (service.equalsIgnoreCase("E")) {
					System.out.print("Nome file: ");
					String file = stdIn.readLine();

					boolean ok = false;

					int g = 0;
					System.out.print("Numero riga: ");
					while (ok != true) {
						g = Integer.parseInt(stdIn.readLine());
						if (g < 0) {
							System.out.println("Numero minore di 0");
							System.out.print("Numero riga: ");
							continue;
						} else
							ok = true;
					}
					System.out.println("Ecco il programma: ");
					FileLunghezza f = serverRMI.elimina_riga(file, g);
					System.out.println("Il file modificato " + f.nomeFile + " ha " + f.numeroRigheTotali + " righe");
					
				} 
				else
					System.out.println("Servizio non disponibile");

				System.out.print("Servizio (C=conta_righe, E=elimina_riga): ");
			} // !EOF

		} catch (Exception e) {
			System.err.println("ClientRMI: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

	}

}