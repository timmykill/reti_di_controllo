package esercitazione2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class FileUtility {

	static protected void trasferisci_file_binario(long filedim, DataInputStream src, DataOutputStream dest) throws IOException {
	    
	    try {
	    	for(int i=0;i<filedim;i++) {
	    		dest.write(src.read());
	    	}
	    	dest.flush();
	    }
	    catch (EOFException e) {
	    	e.printStackTrace();
	    }
}
		
}
