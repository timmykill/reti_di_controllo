import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RegistryRemotoTagImpl extends UnicastRemoteObject implements RegistryRemotoTagServer{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String[] tags= {
			"Medici","Farmacie","Bar","Commercialisti","Parrucchieri",
			"Dentisti","Idraulici","Ristoranti","Supermercati","Congresso"
	};
	
	
	final int tableSize = 100;
	// Tabella: la prima colonna contiene i nomi, la seconda i riferimenti remoti
	Object [][] table = new Object[100][3];
	
	// Costruttore
	public RegistryRemotoTagImpl() throws RemoteException{
		super();
	for( int i=0; i<tableSize; i++ ){ 
		table[i][0]=null; table[i][1]=null;table[i][2]=null; 
		}
	}
	
	//restituisce riferimento al primo server con stesso nome logico
	public synchronized Remote cerca(String nomeLogico) throws RemoteException{ 
		Remote risultato = null;
		if( nomeLogico == null ) return null;
		for( int i=0; i<tableSize; i++ )
			if( nomeLogico.equals((String)table[i][0]) ){
				risultato = (Remote) table[i][1];
				break;
			}
		return risultato;
	}

	//restituisce tutti i riferimenti al nome logico
	public synchronized Remote[] cercaTutti(String nomeLogico) throws RemoteException{
		int cont = 0;
		if( nomeLogico == null ) return new Remote[0];
		for( int i=0; i<tableSize; i++ )
			if( nomeLogico.equals((String)table[i][0]) )
				cont++;
		Remote[] risultato = new Remote[cont];
		// usato come indice per il riempimento
		cont=0;
		for( int i=0; i<tableSize; i++ )
			if( nomeLogico.equals((String)table[i][0]) )
				risultato[cont++] = (Remote)table[i][1];
		return risultato;
	}
	
	//restituisce tutte le coppie nomelogico-riferimento senza i tag
	public synchronized Object[][] restituisciTutti() throws RemoteException{
		int cont = 0;
		for (int i = 0; i < tableSize; i++)
			if (table[i][0] != null) cont++;
		Object[][] risultato = new Object[cont][2];
		// usato come indice per il riempimento
		cont = 0;
		for (int i = 0; i < tableSize; i++)
			if (table[i][0] != null) {
				risultato[cont][0] = table[i][0];
				risultato[cont][1] = table[i][1];
			}
		return risultato;
	}
	
	//aggiunge una coppia
	public synchronized boolean aggiungi(String nomeLogico, Remote riferimento) throws RemoteException{
		boolean result = false;
		// Cerco la prima posizione libera e la riempio
		if((nomeLogico == null)||(riferimento == null))
			return result;
		for(int i=0; i<tableSize; i++)
			if( table[i][0] == null ){
				table[i][0]= nomeLogico; table[i][1]=riferimento;
				result = true;
				break;
			}
		return result;
	}

	//rimuove il primo
	public synchronized boolean eliminaPrimo(String nomeLogico) throws RemoteException{
		boolean risultato = false;
		if( nomeLogico == null ) return risultato;
		for( int i=0; i<tableSize; i++ )
			if( nomeLogico.equals( (String)table[i][0]) )
			{ table[i][0]=null; table[i][1]=null;table[i][2]=null; risultato=true;
			break;
			}
		return risultato;
	}

	
	public synchronized boolean eliminaTutti(String nomeLogico) throws RemoteException{
		boolean risultato = false;
		if( nomeLogico == null ) return risultato;
		for( int i=0; i<tableSize; i++ )
			if( nomeLogico.equals((String)table[i][0]) )
			{ if( risultato == false ) risultato = true;
			table[i][0]=null;
			table[i][1]=null;
			table[i][2]=null;
			}
		return risultato;
	}

	
	public static void main (String[] args) {
		int registryRemotoPort = 1099;
		String registryRemotoHost = "localhost";
		String registryRemotoName = "RegistryRemoto";
		if (args.length != 0 && args.length != 1) // Controllo args
		{ System.out.println("..."); System.exit(1); }
		if (args.length == 1)
		{ try {registryRemotoPort =Integer.parseInt(args[0]); }
		catch (Exception e) {e.printStackTrace();}
		}
		// Registrazione RegistryRemoto presso rmiregistry locale
		String completeName = "//" + registryRemotoHost + ":" +
				registryRemotoPort + "/" + registryRemotoName;
		try
		{ RegistryRemotoTagImpl serverRMI =
		new RegistryRemotoTagImpl();
		Naming.rebind(completeName, serverRMI);
		} catch (Exception e) {e.printStackTrace();}
	}

	//restituisce tutti i riferimenti che contengono il tag specificato
	@Override
	public synchronized String[] cercaTag(String tag) throws RemoteException {
		if(tag==null || !this.validTag(tag))
			throw new RemoteException();
		
		int cont=0;//numero di riferimenti da restituire
		for(int i=0;i<this.tableSize;i++) {
			if(this.table[i][0]!=null && this.table[i][2]!=null) {//in base al metodo eliminaprimo ci possono essere null anche in mezzo
				String[] tmp = (String[]) this.table[i][2];
				for(int j=0;j<tmp.length;j++) {
					if(tmp[j].equals(tag)) {
						cont++;
						break;
					}
				}
			}
		}
		if(cont==0)
			return null;
		//ho contato quanti riferimenti restituire
		String[] risultato = new String[cont];
		cont=0;
		for(int i=0;i<this.tableSize;i++) {
			if(this.table[i][0]!=null && this.table[i][2]!=null) {//in base al metodo eliminaprimo ci possono essere null anche in mezzo
				String[] tmp = (String[]) this.table[i][2];
				for(int j=0;j<tmp.length;j++) {
					if(tmp[j].equals(tag)) {
						risultato[cont++] = (String)table[i][0];
						break;
					}
				}
			}
		}
		return risultato;
	}
	
	private boolean validTag(String tag) {
		for(int i=0;i<tags.length;i++)
			if(tag.equals(tags[i]))
				return true;
		return false;
	}
	//aggiunge il tag specificato alla lista di tag associata al nome logico(possono esserci piu nomi logici uguali)
	@Override
	public synchronized boolean associaTag(String nome_logico_server, String tag) throws RemoteException {
		
		if(tag==null || nome_logico_server==null || !this.validTag(tag))
			throw new RemoteException();
		
		boolean fatto=false;
		String[] tmptags=null;
		String[] newtags=null;
		
		for(int i=0;i<this.tableSize;i++) {
			if(this.table[i][0]!=null && this.table[i][0].equals(nome_logico_server)) {
				if(this.table[i][2]==null) {
					newtags=new String[1];
					newtags[0]=tag;
					fatto=true;
					this.table[i][2]=newtags;
				}
				else {
					tmptags=(String[]) this.table[i][2];
	 				newtags=new String[tmptags.length+1];
	 				for(int j=0;j<tmptags.length;j++) {
	 					newtags[j]=tmptags[j];
	 				}
	 				newtags[tmptags.length]=tag;
	 				fatto=true;
	 				this.table[i][2]=newtags;
				}
				
			}
		}
		if(fatto)
			this.stampa();
		return fatto;//true se lo ha aggiunto ad almeno un nome logico
	}
	
	private String stampaTag(String[] tags) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<tags.length;i++)
			if(tags[i]!=null)
				sb.append(tags[i]);
		return sb.toString();
	}
	
	public void stampa() {
		System.out.println("Ecco il contenuto aggiornato del registry:\n");
		for(int i=0;i<this.tableSize;i++) {
			if(this.table[i][0]!=null) {
				System.out.println("Nome logico n."+(i+1)+(String)this.table[i][0]+
						((this.table[i][2]!=null)?", con tag:\n "+stampaTag((String[])table[i][2]):""));
			}
		}
	}

}
