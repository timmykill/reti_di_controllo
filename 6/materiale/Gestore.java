import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;

public class Gestore implements Serializable {

	public Gestore() {
		super();
	}

	public synchronized int conta(String nomeFile, int n) throws IOException {
		int result = 0;

		BufferedReader buff = new BufferedReader(new FileReader(nomeFile));
		System.out.println("Conto numero righe con + di " + n + " parole in" + nomeFile);
		String line;
		while ((line = buff.readLine()) != null) {
			String[] token = line.split(" ");
			if (token.length >= n) {
				result++;
			}
		}
		buff.close();

		return result;

	}

	public synchronized FileLunghezza elimina_riga(String nomeFile, int numeroRiga) throws IOException {
		int i = 1;
		FileLunghezza result = null;

		File tmp = new File("tmp");
		BufferedWriter out = new BufferedWriter(new FileWriter(tmp));

		BufferedReader buff = new BufferedReader(new FileReader(nomeFile));
		System.out.println("Elimino riga " + numeroRiga + " da" + nomeFile);
		String line;
		while ((line = buff.readLine()) != null) {
			if (i != numeroRiga) {
				out.write(line + "\n");
			}
			i++;
		}
		buff.close();
		out.close();
		if (i < numeroRiga) {
			throw new RemoteException();
		} else {
			tmp.renameTo(new File(nomeFile));
			result = new FileLunghezza(nomeFile, i--);

		}

		return result;
	}

}
