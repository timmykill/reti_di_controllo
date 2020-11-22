
import java.io.*;

public class FileLunghezza implements Serializable {

	private static final long serialVersionUID = 8887258662327145191L;
	public String nomeFile;
	public int numeroRigheTotali;

	public FileLunghezza(String nomeFile, int numeroRigheTotali) {
		super();
		this.nomeFile = nomeFile;
		this.numeroRigheTotali = numeroRigheTotali;
	}

}