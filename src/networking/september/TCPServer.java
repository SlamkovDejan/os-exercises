package networking.september;

// https://imgur.com/a/2WX45MO?fbclid=IwAR1LO1zGNrSFvglStd46cZMH9RJV2-e6JlCIm6b0t3c7sVW-PMW7EoK_7WY

class Server{

    public Server(String clientsDataFilePath){

    }

    public void takeRequests(){
        while (true){

        }
    }

    public void close(){

    }

}

public class TCPServer {

    public static int SERVER_PORT = 3398;

    public static void main(String[] args) {
        String clientsDataFilePath = "src/networking/september/data/clients_data.txt";
        Server server = null;
        try{
            server = new Server(clientsDataFilePath);
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
