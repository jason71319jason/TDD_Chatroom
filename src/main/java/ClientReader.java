import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Handler;

public class ClientReader extends Thread {

    private BufferedReader reader;
    private Client client;

    public ClientReader(Client client,
                        BufferedReader reader) {
        this.client = client;
        this.reader = reader;
    }

    @Override
    public void run() {
        String response;
        while(true) {

            // Read from console
            try {
                response = reader.readLine();
            } catch (IOException e) {
                System.out.println("Server accidentally closed. [IOException]");
                this.client.disconnect();
                e.printStackTrace();
                break;
            }

            // check response does not equal NULL
            if (response == null) {
                System.out.println("Server accidentally closed. [NULL Response]");
                this.client.disconnect();
                break;
            }

            // handle response
            this.responseHandler(response);
        }
    }

    public void responseHandler(String msg) {
        this.client.getLogger().info(msg);

        Message receivedMessage = new Message();
        receivedMessage.setMessageByJson(new JSONObject(msg));

        switch(receivedMessage.getMessageType()) {
            case WHISPER:
            case GLOBAL:
                // name is empty means has not join chat room yet
                if (!this.client.getClientInfo().getName().isEmpty()) {
                    System.out.println(receivedMessage.content);
                }
                break;
            default:
                this.client.getLogger().warning("Error: Client receive " +
                        receivedMessage.getMessageType() + " type message");
                break;
        }
    }
}
