package networking.september;

class Client{

    public Client(String folderOutput){

    }

    // looks for the files that meet the criteria and writes lines into the .out file
    public void iterate(String folderPath){

    }

    // sends the number of characters in the .out file to the server
    public void sendDataToServer(){

    }

    public void close(){

    }

}

public class TCPClient {

    public static void main(String[] args) {
        String folderTxtOutput = "src/networking/september/data";
        String folderToIterate = "src/io/data";

        Client client = null;
        try{
            client = new Client(folderTxtOutput);
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
