package com.company;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;

public class FileUtility {

    /* Sono sicuro al 100% che esistano metodi migliori per far sta roba */
    static protected void trasferisciAByteFileBinario (DataInputStream src, DataOutputStream dest) throws IOException {
        int buffer;
        try {
            while ((buffer = src.read()) >= 0)
                dest.write(buffer);
            dest.flush();
        } catch (EOFException e){
            e.printStackTrace();
        }
    }
}
