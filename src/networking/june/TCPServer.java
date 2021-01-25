package networking.june;

// https://imgur.com/a/2Y44V38?fbclid=IwAR2yEJdOgTHt3ZEzvwq9jWQYcQ16BpSLE0BikCmKWVmx0A2i-IhUkpa1HR0

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

class Server{

    private ServerSocket serverSocket;
    private File clientsData;

    public Server(String dataFilePath, int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.clientsData = new File(dataFilePath);
    }

    public void takeRequests(){
        while (true){
            try {
                Socket socket = this.serverSocket.accept();
                ServerWorkerThread swt = new ServerWorkerThread(socket, this.clientsData);
                swt.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() throws IOException {
        if(this.serverSocket != null){
            this.serverSocket.close();
        }
    }

    static class ServerWorkerThread extends Thread{

        private Socket client;
        private File clientsData;
        private static Semaphore semaphore = new Semaphore(1);

        public ServerWorkerThread(Socket socket, File clientsData) {
            this.client = socket;
            this.clientsData = clientsData;
        }

        @Override
        public void run() {
            DataInputStream dis = null;
            PrintWriter pw = null;
            try {
                InputStream readFromClient = this.client.getInputStream();
                dis = new DataInputStream(readFromClient);
                long size = dis.readLong();
                long lastModified = dis.readLong();

                String clientAddress = this.client.getInetAddress().getHostAddress();
                int clientPort = this.client.getPort();

                String line = clientAddress + " " + clientPort + " " + size + " " + lastModified;

                semaphore.acquire();
                pw = new PrintWriter(new FileOutputStream(clientsData, true));
                pw.println(line);
                pw.flush();
                semaphore.release();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(dis != null){
                    try {
                        dis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(pw != null){
                    pw.flush();
                    pw.close();
                }
            }
        }

    }

}

public class TCPServer {

    public static int SERVER_PORT = 3398;

    public static void main(String[] args) throws IOException {
        String dataFilePath = "src/networking/june/data/_data.txt";
        Server server = null;
        try{
            server = new Server(dataFilePath, SERVER_PORT);
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
