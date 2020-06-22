import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Handler;

public class ClientReader extends Thread {

    private BufferedReader reader;
    private Socket socket;
    private ClientInfo clientInfo;
    private Client client;

    public ClientReader(Client client,
                        BufferedReader reader) {
        this.socket = client.getSocket();
        this.clientInfo = client.getClientInfo();
        this.client = client;
        this.reader = reader;
    }

    @Override
    public void run() {
        String response;
        do {
            try {
                response = reader.readLine();
                this.responseHandler(response);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        } while(response != null);
    }

    public void responseHandler(String msg) {
        this.client.getLogger().info(msg);

        Message receivedMessage = new Message();
        Message sentMessage = new Message();
        receivedMessage.setMessageByJson(new JSONObject(msg));

        switch(receivedMessage.getMessageType()) {
            case WHISPER:
            case GLOBAL:
                // name is empty means has not join chat room yet
                if (!this.clientInfo.getName().isEmpty()) {
                    System.out.println(receivedMessage.content);
                }
                break;
            case SERVER:
                if (receivedMessage.content.equals("REGISTER_OK")) {
                    // set client username
                    this.clientInfo.setName(receivedMessage.
                            getReceivers()[0]);
                } else if (receivedMessage.content.equals("REGISTER_FAILED")) {
                    System.err.println("Register failed, the name had been used by others.");
                }
                break;

            default:
                this.client.getLogger().warning("Error: Client receive " +
                        receivedMessage.getMessageType() + " type message");
                break;
        }
    }
}
