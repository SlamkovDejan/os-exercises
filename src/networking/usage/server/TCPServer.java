package networking.usage.server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class Server {

    private ServerSocket serverSocket;
    private File clientsFolder;

    public Server(int port, String folderPath) throws IOException {
        // this constructor of 'ServerSocket' allows a 'ServerSocket' object to 'bind' to a certain port and
        // by default it binds the object to all available ip addresses on the machine, including localhost (127.0.0.1)
        // 'bind' -> listen for connections
        this.serverSocket = new ServerSocket(port);
        System.out.printf("Server listens on port %d, on every ip address on the machine\n", port);

        // the folder where we'll save the incoming client files
        this.clientsFolder = new File(folderPath);
        if(!this.clientsFolder.exists()) {
            this.clientsFolder.mkdirs();
        }
    }

    public void takeRequests(){

        // the server takes requests all the time, i.e. is active non stop
        while (true){
            try{
                // 'accept()' returns a 'Socket' object which represents the 'pipe' between a client and the server
                // 'accept()' puts the thread in blocked state until a connection arrives and
                // returns (running state) the moment a client has connected to the server (through his ip and port)
                Socket client = this.serverSocket.accept(); // we work with the client bellow this line
                logInfo(client);
                // START A THREAD
                ServerWorkerThread swt = new ServerWorkerThread(client);
                swt.start();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void logInfo(Socket client){
        // the local machine is the machine where the code runs

        // 'InetAddress' is a wrapper class over the 32bit (IPv4) or 128bit (IPv6) Internet addresses
        InetAddress clientAddress = client.getInetAddress(); // returns the address of the remote machine, in this case the client
        InetAddress serverAddress = client.getLocalAddress(); // returns the address of the local machine, in this case the server

        // 'getHostAddress()' returns a String representation of the address (ex. 127.0.0.1)
        String clientStringAddress = clientAddress.getHostAddress();
        // String clientStringAddress = client.getInetAddress().getHostAddress();
        String serverStringAddress = serverAddress.getHostAddress();

        int clientPort = client.getPort(); // returns the port of the remote machine, in this case the client
        int serverPort = client.getLocalPort(); // returns the port of the local machine, in this case the server

        System.out.printf(
                "A client (%s:%d) has connected to the server (%s:%d)\n",
                clientStringAddress, // the string representation of the address
                clientPort,
                serverStringAddress,
                serverPort
        );
    }

    public void close() throws IOException {
        if(this.serverSocket != null){
            this.serverSocket.close();
        }
    }

    class ServerWorkerThread extends Thread{

        private Socket client;

        public ServerWorkerThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            // next we work on providing the service to the client (saving a .txt file)
            // there needs to be a protocol of communication before anything else can begin, so before writing the code
            // there need to be an agreement between the the parties involved on how the data will be sent and received
            // if there is no protocol (agreement) the communication may fail (deadlock situation)

            // the protocol in this situation is as follows:
            // the client first sends the name of the file it wants to save here on the server
            // next the client send the file over to the server, line by line
            // the server receives the lines and saves them to the file on it's new location here on the server (in the 'clientsFolder' folder)
            // when the client finishes with the sending of the file, it sends the message "END" to notify the server
            // when the server gets the message "END" it stops, saves the file, and send back the message "DONE" to the client
            // the client can terminate when it gets the message "DONE", which means the job is done


            // explanations bellow :)
            BufferedReader clientBF = null;
            PrintWriter filePW = null;
            PrintWriter clientPW = null;

            try{
                // '.getInputStream()' retrieves the input stream to the socket (pipe)
                // everything that is read from this stream came from the client through the network (internet)
                InputStream readFromClient = client.getInputStream(); // only working with bytes
                // '.getOutputStream()' retrieves the output stream to the socket (pipe)
                // everything that is written to this stream, will be sent to the client through the network (internet)
                OutputStream writeToClient = client.getOutputStream(); // only working with bytes

                // because we don't want to work with bytes, we use other classes that offer better methods
                // plus the protocol says that the client will send the file line by line so, 'BufferedReader' it is
                // this 'BufferedReader' will read from the input stream for the socket
                clientBF = new BufferedReader(new InputStreamReader(readFromClient));

                // the protocol said that the client will send the name first
                // blocks until it receives it
                String clientFileName = clientBF.readLine();
                File clientFile = new File(clientsFolder, clientFileName); // clientsFile -> 'path/to/clients/folder/clientFileName.txt'
                clientFile.createNewFile(); // it does not exist, so we must create it

                // we need to write every line from the client to the local file on the server ('clientFile')
                // meaning that we need to open an outputStream/writer with that file as the data source and use the methods to write the line
                // we will use the 'PrintWriter' method: 'println(lineFromClientFile)'
                filePW = new PrintWriter(clientFile);

                // next we need to read the lines that come from the client
                while (true){
                    String lineFromClientFile = clientBF.readLine();
                    if(lineFromClientFile.equals("END")){ // the protocol
                        break;
                    }
                    filePW.println(lineFromClientFile);
                }
                filePW.flush();

                // we must now notify the client that the file has been saved successfully
                // we need to send "DONE" (a string) to the client
                // can i use the 'filePW' writer? - No. The data source of that writer is the file
                // can i use the 'writeToClient' output stream? - Yes. The data source of that stream is the client
                // but one thing, 'writeToClient' is of type 'OutputStream', which we know it only works with bytes (we need to send a string)
                // - that's okay, we'll wrap that stream into a another stream/writer that will offer us a method that writes a string
                // hmmmm... -> 'PrintWriter' of course, but we will use another reference (not the 'filePW' one)
                clientPW = new PrintWriter(writeToClient);
                clientPW.println("DONE");
                clientPW.flush(); // A MUST

                System.out.println("Done with the client, file is saved.");

                // that's it, we're done with this client... But, is this the right way to deal with clients?
            } catch (Exception e){
                e.printStackTrace();
            } finally {
                if(clientBF != null){
                    try {
                        clientBF.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(filePW != null){
                    filePW.flush();
                    filePW.close();
                }
                if(clientPW != null){
                    clientPW.flush();
                    clientPW.close();
                }
            }
        }
    }

}


public class TCPServer {

    public static int SERVER_PORT = 5555;

    public static void main(String[] args) throws IOException {
        String pathToFolder = "src/networking/usage/server/data";
        Server server = null;
        try{
            server = new Server(SERVER_PORT, pathToFolder);
            server.takeRequests();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(server != null){
                server.close();
            }
        }
    }
}
