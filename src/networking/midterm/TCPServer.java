package networking.midterm;

/*
Клиентот ја пресметува вкупната големина на сите '.png' фајлови во фолдер чија патека ќе му биде предадена преку
аргумент на методата 'calculateSizeOfPNG()'. Добиената сума ја праќа на сервер. Серверот таа бројка, заедно со адресата и портата на клиентот ги
запишува во датотеката "filesizes.txt" која се наоѓа во фолдерот кој ќе му биде предаден како аргумент. Еден ред во
"filesizes.txt" датотеката треба да биде во формат: <ip-client> <port-client> <size>.

Да се овозможи повеќе клиенти истовремено да можат да комуницираат со серверот.
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

class Server{

    private ServerSocket serverSocket;
    private File fileSizes;

    public Server(String folderPath, int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.fileSizes = new File(folderPath, "filesizes.txt");
    }

    public void takeRequests(){
        while (true){
            try {
                Socket client = this.serverSocket.accept();
                // CREATE THREAD
                ServerWorkerThread swt = new ServerWorkerThread(client, this.fileSizes);
                // START THREAD
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
        private File fileSizes;
        private static Semaphore semaphore = new Semaphore(1);

        public ServerWorkerThread(Socket client, File fileSizes) {
            this.client = client;
            this.fileSizes = fileSizes;
        }

        @Override
        public void run() {
            DataInputStream dis = null;
            PrintWriter pw = null;
            try {
                InputStream readFromClient = this.client.getInputStream();
                dis = new DataInputStream(readFromClient);
                long total = dis.readLong();

                String clientAddress = this.client.getInetAddress().getHostAddress();
                int clientPort = this.client.getPort();

                String line = clientAddress + " " + clientPort + " " + total;

                semaphore.acquire();
                pw = new PrintWriter(new FileOutputStream(fileSizes, true));
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

    public static int SERVER_PORT = 5555;

    public static void main(String[] args) throws IOException {
        String folderPath = "src/networking/midterm/data";
        Server server = null;
        try{
            server = new Server(folderPath, SERVER_PORT);
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
