import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler extends Thread {

    public static final String SERVER_NAME = "SERVER";
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
        while(true) {

            // read from socket
            try {
                clientMsg = reader.readLine();
            } catch (IOException e) {
                this.close();
                e.printStackTrace();
                break;
            }

            // check whether socket alive
            if(clientMsg == null) {
                this.close();
                break;
            }

            handleMessage(clientMsg);
        }
    }

    public void close() {

        this.server.getServerHandles().remove(this);
        String Response =  this.clientName + " leave the chat room";
        Message sentMessage = new Message();
        sentMessage.setMessage(this.clientName,
                new String[]{},
                MessageType.GLOBAL,
                Response);
        this.broadcastMessage(sentMessage.getJsonString());

        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMessage(String msg) {

        this.server.getLogger().info(msg);

        Message receivedMessage = new Message();
        Message sentMessage = new Message();
        receivedMessage.setMessageByJson(new JSONObject(msg));

        String Response;

        switch(receivedMessage.getMessageType()) {
            case WHISPER:
                Response = this.clientName + " whispers: " + receivedMessage.getContent();
                sentMessage.setMessage(this.clientName,
                        receivedMessage.receivers,
                        MessageType.WHISPER,
                        Response);
                this.whisperMessage(receivedMessage.getReceivers()[0], sentMessage.getJsonString());
                break;

            case GLOBAL:
                Response = this.clientName + " says: " + receivedMessage.getContent();
                sentMessage.setMessage(this.clientName,
                        receivedMessage.receivers,
                        MessageType.GLOBAL,
                        Response);
                this.broadcastMessage(sentMessage.getJsonString());
                break;

            case QUIT:
                this.close();
                break;

            case REGISTER:
                String name = receivedMessage.content;
                // check name is valid
                boolean isPass = false;
                if (checkUserName(name)) {
                    this.clientName = name;
                    isPass = true;
                    Response = "REGISTER_OK";
                } else {
                    Response = "REGISTER_FAILED";
                }

                sentMessage.setMessage(ServerHandler.SERVER_NAME,
                        new String[]{receivedMessage.content},
                        MessageType.SERVER,
                        Response);
                this.sendMessage(sentMessage.getJsonString());

                // broadcast to other
                if(isPass) {
                    Response = this.clientName + " joins the chat room";
                    sentMessage.setMessage(ServerHandler.SERVER_NAME,
                            receivedMessage.receivers,
                            MessageType.GLOBAL,
                            Response);
                    this.broadcastMessage(sentMessage.getJsonString());
                }
                break;

            case SERVER:
            default:
                this.server.getLogger().warning("Server receive " +
                        receivedMessage.getMessageType() + " type message");
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

    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
