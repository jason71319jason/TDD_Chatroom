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
                JSONObject clientRegisterInfo = new JSONObject(clientMsg);
                clientName = clientRegisterInfo.getString("sender");
//                System.out.println(clientName);
                System.out.println(clientMsg);
                this.broadcast(clientMsg);
            } while(!clientMsg.equals("/exit"));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
