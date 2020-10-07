package prod_cons;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Produttore {

	public static boolean isInt(String intero) {//uso un pò birikkino delle eccezioni, chissà quante regole di ingegneria del software ho violato? se alcune lib di java possono trattare EOF come eccezione anche questo è lecito :P
		try {
			Integer.parseInt(intero);
		}catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		int filelen = args.length, colPosition, fileToWrite;
		BufferedReader in = null;
		String input = null, line;
		if(filelen == 0) {
			System.out.println("bad arguments");
			System.exit(1);
		}
		FileWriter []fout = new FileWriter[filelen];
		for(int i=0;i < filelen;i++) {
			try {
				fout[i] = new FileWriter(args[i]);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("error in file opening");
				System.exit(2);
			}
		}
		in = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.println("inserisci la riga");
			while((input = in.readLine())!=null) {
				if((colPosition = input.indexOf(':',1)) < 0 || input.charAt(0) == '0' || !isInt(input.substring(0, colPosition)) || (fileToWrite = Integer.parseInt(input.substring(0, colPosition))) > filelen) {
					System.out.println("riga malformata, riprova");
				}else {
					line = input.substring(colPosition+1)+"\n";
					fout[fileToWrite-1].write(line, 0, line.length());
				}
				System.out.println("inserisci la riga");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("errore in lettura input");
			System.exit(3);
		}

		for(int i = 0;i < filelen;i++) {
			try {
				fout[i].close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("errore in chiusura file");
				System.exit(4);
			}
		}
	}

}
