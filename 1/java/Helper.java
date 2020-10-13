package esercitazioneProposta1;

import java.io.*;

public class Helper {
    public static String bytesToStringUTF (byte[] bytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        return dataInputStream.readUTF();
    }

    public static byte[] stringUTFToBytes (String string) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeUTF(string);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] intToBytes(int integer) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(integer);
        return byteArrayOutputStream.toByteArray();
    }

    public static int bytesToInt(byte[] bytes) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        return dataInputStream.readInt();
    }

//    public static String bytesToStringUTF (byte[] bytes) throws IOException {
//        return new String(bytes, StandardCharsets.UTF_8);
//    }
//
//
//    public static byte[] stringUTFToBytes (String string) throws IOException {
//        return string.getBytes(StandardCharsets.UTF_8);
//    }

}
