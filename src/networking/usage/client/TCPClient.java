package networking.usage.client;

import networking.usage.server.TCPServer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

class Client{

    private Socket connection;

    public Client(InetAddress serverAddress, int serverPort) throws IOException {
        // the 'Socket' class takes the address and port of the server to which it needs to connect to as arguments to it's constructor
        // the address is of type 'InetAddress', the wrapper of the internet addresses
        this.connection = new Socket(serverAddress, serverPort);
        // here
        logInfo();
    }

    private void logInfo(){
        // the local machine is the machine where the code runs

        // 'InetAddress' is a wrapper class over the 32bit (IPv4) or 128bit (IPv6) Internet addresses
        InetAddress serverAddress = connection.getInetAddress(); // returns the address of the remote machine, in this case the server
        InetAddress clientAddress = connection.getLocalAddress(); // returns the address of the local machine, in this case the client

        // 'getHostAddress()' returns a String representation of the address (ex. 127.0.0.1)
        String serverStringAddress = serverAddress.getHostAddress();
        // String serverStringAddress = connection.getInetAddress().getHostAddress();
        String clientStringAddress = clientAddress.getHostAddress();

        int serverPort = connection.getPort(); // returns the port of the remote machine, in this case the server
        int clientPort = connection.getLocalPort(); // returns the port of the local machine, in this case the client

        System.out.printf(
                "A client (%s:%d) has connected to the server (%s:%d)\n",
                clientStringAddress, // the string representation of the address
                clientPort,
                serverStringAddress,
                serverPort
        );
    }

    public void sendTxtFile(String pathToTxtFile, String serverFileName) throws IOException {
        // READ PROTOCOL IN THE COMMENTS IN THE SERVER CLASS

        PrintWriter serverPW = null;
        BufferedReader serverBF = null;
        BufferedReader fileBF = null;

        try{
            // first we need to write the name of file on the server side, which is: 'serverFileName'
            // for that we need the output stream to write to the server (connection.getOutputStream())
            // but, as we know that method will return a reference of type 'OutputStream' which only works with bytes
            // that's why we'll use a 'PrintWriter' and we'll make sure that we will write '\n' on the end
            // because the server reads this line with the method 'clientBF.readLine()', which reads until a '\n' is encountered

            OutputStream writeToServer = this.connection.getOutputStream();
            serverPW = new PrintWriter(writeToServer);
            serverPW.println(serverFileName); // \n
//            // other way
//            serverPW.print(serverFileName); // does not write a '\n' on the end
//            serverPW.println(); // so we must write it out selves
            serverPW.flush(); // A MUST

            // next we send our file to the server, line by line
            // for that we will use a 'BufferedReader' (.readLine()) which will have the file as the data source
            fileBF = new BufferedReader(new FileReader(pathToTxtFile));
            while (true){
                String lineFromFile = fileBF.readLine();
                if(lineFromFile == null){ // the end of file has been reached
                    serverPW.println("END"); // by protocol
                    break;
                }
                serverPW.println(lineFromFile); // there must be a '\n' at the end
            }
            serverPW.flush();

            // next we need to make sure that the file has been saved (server will send "DONE" by protocol)
            // so we will need to read the "DONE" from the server, meaning we will need the 'InputStream' from the 'pipe'
            // we can't use 'fileBF' for reasons already explained (in the Server class), so we will use another reference
            InputStream readFromServer = this.connection.getInputStream();
            serverBF = new BufferedReader(new InputStreamReader(readFromServer));
            String done = serverBF.readLine();

            System.out.printf("File has been saved: %s\n", done);

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(serverBF != null){
                serverBF.close();
            }
            if(fileBF != null){
                fileBF.close();
            }
            if(serverPW != null){
                serverPW.flush();
                serverPW.close();
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
        String pathToTxtFile = "src/networking/usage/client/data/deki.txt";
        String serverFileName = "deki_on_server_1.txt";
        Client client = null;
        try{
            // InetAddress.getLocalHost() -> returns a wrapper over the localhost address (127.0.0.1)
            // we want a wrapper over the localhost address because the server listens on that address
            client = new Client(InetAddress.getLocalHost(), TCPServer.SERVER_PORT);
            client.sendTxtFile(pathToTxtFile, serverFileName);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            client.close();
        }

    }

}
