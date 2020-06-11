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

    public ClientReader(Socket socket, ClientInfo clientInfo, Client client) {
        this.socket = socket;
        this.clientInfo = clientInfo;
//        this.client = client;
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
//                System.out.println("Reader: " + clientInfo.getName());
                responseHandler(response);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void responseHandler(String response) {
        System.out.println("\n" + response);
        JSONObject serverResponse = new JSONObject(response);
        MessageType messageType = MessageType.valueOf(serverResponse.getString("messageType"));
//        System.out.println('\n' + serverResponse.getString("messageType"));
        String content = serverResponse.getString("content");
//        System.out.println("content: " + content);
        switch(messageType) {
            case WHISPER:
                break;
            case GLOBAL:
                // name is empty means has not join chat room yet
                if (!clientInfo.getName().isEmpty()) {
                    System.out.println(content);
                }
                break;
            case SERVER:
                if (content.equals("REGISTER OK")) {
//                    System.out.println("OK");
                    // set client username
                    JSONArray name = serverResponse.getJSONArray("receivers");
//                    System.out.println(name.getString(0));
                    clientInfo.setName(name.getString(0));
                } else if (content.equals("REGISTER failed")) {
                    System.out.println("Register failed, the name had been used by others.");
                }
                break;
            default:
                System.out.println("Error: Client receive " + messageType + " type message");
                break;
        }
    }
}
