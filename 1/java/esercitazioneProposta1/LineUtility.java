package esercitazioneProposta1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LineUtility {
		
	public static int swapLine(String file, int linea1, int linea2) throws FileNotFoundException, IOException{
		
		if(linea1 == linea2)
			return -1;
		//
		BufferedReader in = null;
		BufferedWriter out = null;
		StringBuilder sb = new StringBuilder();
		
			in = new BufferedReader(new FileReader(file));
			in.mark(0);
		
		int i = 1;
		String l1 = null;
		String l2 = null;
		String l;
			while((l = in.readLine()) != null) {
				if(i == linea1) {
					l1 = l;
				}else if(i == linea2) {
					l2 = l;
				}
				i++;
			}
			in.close();
		
		if((l1 == null) || (l2 == null)) {
			return -1;
		}else {
				in = new BufferedReader(new FileReader(file));
				i = 1;
				while((l = in.readLine()) != null) {
					if(i == linea1) {
						sb.append(l2);
					}else if(i == linea2) {
						sb.append(l1);
					}else {
						sb.append(l);
					}
					sb.append("\n");
					i++;
				}
				in.close();
				out = new BufferedWriter(new FileWriter(file));
				out.write(sb.toString());
				out.flush();
				out.close();
			return 0;
		}
		
	}
	
}
