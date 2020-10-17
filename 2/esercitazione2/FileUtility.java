package esercitazione2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class FileUtility {

	static protected void trasferisci_a_byte_file_binario(Long filedim, DataInputStream src, DataOutputStream dest) throws IOException {
	
	    int buffer;    
	    Long cont=(long) 0;
	    
	    try {
	    	while ((buffer=src.read()) >= 0 && cont<filedim) {
	    		cont++;
	    		dest.write(buffer);
	    	}
	    	dest.flush();
	    }
	    catch (EOFException e) {
	    	System.out.println("Problemi, i seguenti: ");
	    	e.printStackTrace();
	    }
	}
}
