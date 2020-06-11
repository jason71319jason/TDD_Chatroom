import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler extends Thread {

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Server server;
    private String clientName;

    public ServerHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            // register


            String clientMsg;
            do {
                clientMsg = reader.readLine();

                ClientHandler(clientMsg);


                System.out.println(clientMsg);
//                this.broadcast(clientMsg);
            } while(!clientMsg.equals("/exit"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ClientHandler(String Msg) {
        JSONObject clientMsg = new JSONObject(Msg);
        MessageType messageType = MessageType.valueOf(clientMsg.getString("messageType"));
        String content = clientMsg.getString("content");
        String Response;
        switch(messageType) {
            case WHISPER:
                break;
            case GLOBAL:
                String broadcastMsg = clientName + " says: " + content;
                Response = createMessage(broadcastMsg, MessageType.GLOBAL);
                this.broadcast(Response);
                break;
            case SERVER:
                break;
            case REGISTER:
                String name = content;
                // check name is valid
                if (checkName(name)) {
                    clientName = name;
                    Response = createMessage("REGISTER OK", MessageType.SERVER);
                } else {
                    Response = createMessage("REGISTER failed", MessageType.SERVER);
                }
                sendMessage(Response);
                break;
            default:
                System.out.println("Error: Server receive " + messageType + " type message");
                break;
        }

    }

    private boolean checkName(String name) {
        for(ServerHandler handler : Server.serverHandlers) {
            if (name.equals(handler.getClientName())) {
                return false;
            }
        }
        return true;
    }

    public void sendMessage(String msg) {
        this.writer.println(msg);
    }

    public void broadcast(String msg) {
        for(ServerHandler handler : Server.serverHandlers) {
            if (this != handler) {
                handler.sendMessage(msg);
            }
        }
    }

    public String createMessage(String msg, MessageType type) {

        // TODO: Message parser
        Message message = new Message();
        message.sender = "SERVER";
        message.receivers = new String[]{clientName};
        message.content = msg;
        message.messageType = type;
        return message.getJsonString();
    }

    public String getClientName() {
        return clientName;
    }
}
