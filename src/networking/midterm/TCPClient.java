package networking.midterm;

/*
Клиентот ја пресметува вкупната големина на сите '.png' фајлови во фолдер чија патека ќе му биде предадена преку
аргумент на методата 'calculateSizeOfPNG()'. Добиената сума ја праќа на сервер. Серверот таа бројка, заедно со адресата и портата на клиентот ги
запишува во датотеката "filesizes.txt" која се наоѓа во фолдерот кој ќе му биде предаден како аргумент. Еден ред во
"filesizes.txt" датотеката треба да биде во формат: <ip-client> <port-client> <size>.

Да се овозможи повеќе клиенти истовремено да можат да комуницираат со серверот.
 */

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

class Client{

    private Socket connection;

    public Client(InetAddress addressOfServer, int portOfServer) throws IOException {
        this.connection = new Socket(addressOfServer, portOfServer);
    }

    private long calculateSizeOfPNG(String folderPath){
        File folder = new File(folderPath);
        File[] contents = folder.listFiles();
        long sum = 0;
        for(File f : contents){
            if(f.isFile() && f.getName().endsWith(".png")){
                sum += f.length();
            } else if(f.isDirectory()){
                sum += calculateSizeOfPNG(f.getAbsolutePath());
            }
        }
        return sum;
    }

    public void sendTotalSizeToServer(String folderPath) throws IOException {
        long totalSize = this.calculateSizeOfPNG(folderPath);
        DataOutputStream dos = null;
        try {
            OutputStream writeToServer = this.connection.getOutputStream();
            dos = new DataOutputStream(writeToServer);
            dos.writeLong(totalSize);
            dos.flush();
        } catch (IOException e) {
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
        String folderPath = "src/networking/midterm/data";
        Client client = null;
        try{
            client = new Client(InetAddress.getLocalHost(), TCPServer.SERVER_PORT);
            client.sendTotalSizeToServer(folderPath);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(client != null){
                client.close();
            }
        }
    }

}
