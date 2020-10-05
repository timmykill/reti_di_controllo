package esercitazione_0;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Consumatore2 {
	public static void main(String[] args) {
		FileReader r = null;
		char ch;
		String str=null;
		StringBuilder sb=new StringBuilder();
		int x;
		
		if (args.length != 1 && args.length!=2){
			System.out.println("Utilizzo: consumatore carattere [<inputFilename>]");
			System.exit(0);
		}
		
		str=args[0];
	  
		try {
			if(args.length==2)
				r = new FileReader(args[1]);
			else 
				r = new FileReader(FileDescriptor.in);
		} catch(FileNotFoundException e){
			System.out.println("File non trovato");
			System.exit(1);
		}
		
		String line=null;
		try {

			while ((x = r.read()) >= 0) { 
				ch = (char) x;
				sb.append(ch);
				if(ch=='\n'){
					line=sb.toString().replace(str, "");
					System.out.print(line);
				}
			}
			r.close();
		} catch(IOException ex){
			System.out.println("Errore di input");
			System.exit(2);
		}
}
}