import org.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
//               this.broadcast(clientMsg);
            } while(!clientMsg.equals("/exit"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ClientHandler(String Msg) {
        Server.logger.info(Msg);
        JSONObject clientMsg = new JSONObject(Msg);
        MessageType messageType = MessageType.valueOf(clientMsg.getString("messageType"));
        JSONArray jsonArray = clientMsg.getJSONArray("receivers");
        List<String> receivers = new ArrayList<String>();

        for (int i=0; i<jsonArray.length(); i++) {
            receivers.add(jsonArray.getString(i));
        }

        String content = clientMsg.getString("content");
        String Response;
        switch(messageType) {
            case WHISPER:
                String whisperMsg = clientName + " whispers: " + content;
                Response = createWhisperMessage(whisperMsg, MessageType.WHISPER);
                this.sendWhisperMessage(receivers.get(0), Response);
                break;
            case GLOBAL:
                String broadcastMsg = clientName + " says: " + content;
                Response = createMessage(broadcastMsg, MessageType.GLOBAL);
                this.broadcast(Response);
                break;
            case QUIT:
                Server.serverHandlers.remove(this);
                String byeMsg =  clientName + " leave the chat room";
                Response = createMessage(byeMsg, MessageType.GLOBAL);
                this.broadcast(Response);
                for(ServerHandler h: Server.serverHandlers) {
                    System.out.println(h.getClientName());
                }
                break;

            case REGISTER:
                String name = content;
                // check name is valid
                boolean isPass = false;
                if (checkName(name)) {
                    clientName = name;
                    isPass = true;
                    Response = createMessage("REGISTER OK", MessageType.SERVER);
                } else {
                    Response = createMessage("REGISTER failed", MessageType.SERVER);
                }
                sendMessage(Response);
                if(isPass) {
                    Response = createMessage(name + " joins the chat room", MessageType.GLOBAL);
                    this.broadcast(Response);
                }
                break;

            default:
                Server.logger.warning("Server receive " + messageType + " type message");
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

    public void sendWhisperMessage(String username, String msg) {
        for(ServerHandler handler : Server.serverHandlers) {
            if (handler.getClientName().equals(username)) {
                handler.sendMessage(msg);
                break;
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

    public String createWhisperMessage(String msg, MessageType type) {

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
