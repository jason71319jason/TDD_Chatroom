import java.io.*;
import java.net.Socket;

public class ClientWriter extends Thread {

    private PrintWriter writer;
    private BufferedReader reader;
    private Socket socket;
    private ClientInfo clientInfo;
    private Client client;

    public ClientWriter(Socket socket, ClientInfo clientInfo, Client client) {
        this.socket = socket;
        this.clientInfo = clientInfo;
//        this.client = client;
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
        // Register
        while(clientInfo.getName().isEmpty()) {
            try {
                Register();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Communication
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

    private void Register() throws InterruptedException {
        System.out.print("Type your name: ");
        try {
            String name = reader.readLine();
            if (name == null || name == "") {
                System.out.println("name can not be empty");
                return;
            }
            String Msg = createMessage(name, MessageType.REGISTER);
            System.out.println(Msg);
            writer.println(Msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Registering...");
        sleep(1000);
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
