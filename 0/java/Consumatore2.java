package esercitazione_0;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Consumatore2 {
	public static void main(String[] args) {
		BufferedReader r = null;
		String str=null, line=null;
		
		if (args.length != 1 && args.length!=2){
			System.out.println("Utilizzo: consumatore carattere [<inputFilename>]");
			System.exit(0);
		}
		
		str=args[0];
	  
		try {
			if(args.length==2)
				r = new BufferedReader(new FileReader(args[1]));
			else 
				r = new BufferedReader(new FileReader(FileDescriptor.in));
		} catch(FileNotFoundException e){
			System.out.println("File non trovato");
			System.exit(1);
		}
		
		try {

			while ((line= r.readLine()).length() >= 0) { 
				line=line.replace(str, "");
				System.out.print(line);
			}
			r.close();
		} catch(IOException ex){
			System.out.println("Errore di input");
			System.exit(2);
		}
}
}