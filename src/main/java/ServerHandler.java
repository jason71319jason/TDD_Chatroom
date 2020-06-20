import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler extends Thread {

    public static final String SERVER = "SERVER";
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Server server;
    private String clientName;

    public ServerHandler(Socket socket, Server server,
                         BufferedReader reader,
                         PrintWriter writer) {
        this.socket = socket;
        this.server = server;
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run()  {
        String clientMsg;

        do {
            try {
                clientMsg = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            handleMessage(clientMsg);
            System.out.println(clientMsg);
            //this.broadcast(clientMsg);
        } while(!clientMsg.equals("/exit"));
    }

    public void handleMessage(String msg) {

        this.server.getLogger().info(msg);

        Message receivedMessage = new Message();
        Message sentMessage = new Message();
        receivedMessage.setMessageByJson(new JSONObject(msg));

        JSONObject clientMsg = new JSONObject(msg);
        MessageType messageType = MessageType.valueOf(clientMsg.getString("messageType"));

        List<String> receivers = new ArrayList<String>();

        JSONArray jsonArray = clientMsg.getJSONArray("receivers");
        for (int i=0; i<jsonArray.length(); i++) {
            receivers.add(jsonArray.getString(i));
        }

        String content = clientMsg.getString("content");
        String sender = clientMsg.getString("sender");
        String Response;
        switch(messageType) {
            case WHISPER:
                String whisperMsg = clientName + " whispers: " + content;
                Response = createMessage(whisperMsg, MessageType.WHISPER);
                sentMessage.setMessage(this.clientName,
                        receivedMessage.receivers,
                        MessageType.WHISPER,
                        Response);
                this.whisperMessage(receivedMessage.getReceivers()[0], sentMessage.getJsonString());
                break;
            case GLOBAL:
                String broadcastMsg = clientName + " says: " + content;
                sentMessage.setMessage(this.clientName,
                        receivedMessage.receivers,
                        MessageType.GLOBAL,
                        Response);
                this.broadcastMessage(sentMessage.getJsonString());
                break;
            case QUIT:
                this.server.getServerHandles().remove(this);
                String byeMsg =  clientName + " leave the chat room";
                Response = createMessage(byeMsg, MessageType.GLOBAL);
                this.broadcastMessage(Response);
                for(Object obj: this.server.getServerHandles()) {
                    ServerHandler handler = (ServerHandler) obj;
                    System.out.println(handler.getClientName());
                }
                break;
            case REGISTER:
                String name = content;
                // check name is valid
                boolean isPass = false;
                if (checkUserName(name)) {
                    clientName = name;
                    isPass = true;
                    Response = createMessage("REGISTER OK", MessageType.SERVER);
                } else {
                    Response = createMessage("REGISTER failed", MessageType.SERVER);
                }
                sendMessage(Response);
                if(isPass) {
                    Response = createMessage(name + " joins the chat room", MessageType.GLOBAL);
                    this.broadcastMessage(Response);
                }
                break;

            default:
                this.server.getLogger().warning("Server receive " + messageType + " type message");
                break;
        }

    }

    private boolean checkUserName(String name) {
        for(Object obj : this.server.getServerHandles()) {
            ServerHandler handler = (ServerHandler) obj;
            if (name.equals(handler.getClientName())) {
                return false;
            }
        }
        return true;
    }

    public void sendMessage(String msg) {
        this.writer.println(msg);
    }

    public void broadcastMessage(String msg) {
        for(Object obj : this.server.getServerHandles()) {
            ServerHandler handler = (ServerHandler) obj;
            if (this != handler) {
                handler.sendMessage(msg);
            }
        }
    }

    public void whisperMessage(String username, String msg) {
        for(Object obj : this.server.getServerHandles()) {
            ServerHandler handler = (ServerHandler) obj;
            if (handler.getClientName().equals(username)) {
                handler.sendMessage(msg);
                break;
            }
        }
    }

    public String createMessage(String msg, MessageType type) {

        Message message = new Message();
        message.sender = SERVER;
        message.receivers = new String[]{this.clientName};
        message.content = msg;
        message.messageType = type;
        return message.getJsonString();
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
