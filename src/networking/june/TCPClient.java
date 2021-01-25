package networking.june;

// https://imgur.com/a/2Y44V38?fbclid=IwAR2yEJdOgTHt3ZEzvwq9jWQYcQ16BpSLE0BikCmKWVmx0A2i-IhUkpa1HR0

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

class Client{

    private Socket connection;
    private File csv;

    // files.csv -> folderTxtOutput
    public Client(String folderTxtOutput, InetAddress serverAddress, int serverPort) throws IOException {
        this.csv = new File(folderTxtOutput, "files.csv");
        this.csv.createNewFile();
        this.connection = new Socket(serverAddress, serverPort);
    }

    // looks for the files that meet the criteria and writes lines into the .csv file
    public void iterate(String folderPath){
        // path/to/file/text.txt,500
        File folder = new File(folderPath);
        File[] contents = folder.listFiles();
        for(File f : contents){
            if(f.isFile()){
                if((f.getName().endsWith(".txt") || f.getName().endsWith(".dat")) && f.length() < (512 * 1000)){
                    String line = f.getAbsolutePath() + "," + f.length();
                    try {
                        PrintWriter pw = new PrintWriter(new FileOutputStream(this.csv, true));
                        pw.println(line);
                        pw.flush();
                        pw.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else if(f.isDirectory()){
                iterate(f.getAbsolutePath());
            }
        }
    }

    // sends the length and last modified date values of the .cvs file to the server
    public void sendDataToServer() throws IOException {
        long size = this.csv.length();
        long lastModified = this.csv.lastModified();

        DataOutputStream dos = null;
        try{
            dos = new DataOutputStream(this.connection.getOutputStream());
            dos.writeLong(size);
            dos.writeLong(lastModified);
            dos.flush();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(dos != null){
                dos.flush();
                dos.close();
            }
        }

    }

    public void close() throws IOException {
        if(this.connection != null){
            this.connection.close();
        }
    }

}

public class TCPClient {

    public static void main(String[] args) throws IOException {
        String folderTxtOutput = "src/networking/june/data";
        String folderToIterate = "src/io/data";

        Client client = null;
        try{
            client = new Client(folderTxtOutput, InetAddress.getLocalHost(), TCPServer.SERVER_PORT);
            client.iterate(folderToIterate);
            client.sendDataToServer();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(client != null){
                client.close();
            }
        }

    }

}
