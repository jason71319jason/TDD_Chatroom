import java.io.*;
import java.net.Socket;

public class ClientWriter extends Thread {

    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;
    private ClientInfo clientInfo;

    public ClientWriter(Socket socket, ClientInfo clientInfo) {
        this.socket = socket;
        this.clientInfo = clientInfo;
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String msg = null;
//        writer.println(createMessage(msg, MessageType.SYSTEM_REGISTER));
        do {
            try {
                // prompt
                System.out.print("[" + clientInfo.getName() + "]: ");
                msg = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.println(createMessage(msg, MessageType.GLOBAL));
        } while(!msg.equals("/exit"));

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String createMessage(String msg, MessageType type) {

        // TODO: Message parser
        Message message = new Message();
        message.sender = clientInfo.getName();
        message.receivers = new String[]{};
        message.content = msg;
        message.messageType = type;
        return message.getJsonString();
    }
}
