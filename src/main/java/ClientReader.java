import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReader extends Thread {

    private BufferedReader reader;
    private Socket socket;
    private ClientInfo clientInfo;

    public ClientReader(Socket socket, ClientInfo clientInfo) {
        this.socket = socket;
        this.clientInfo = clientInfo;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            try {
                String response = reader.readLine();
                System.out.println("\n" + response);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
