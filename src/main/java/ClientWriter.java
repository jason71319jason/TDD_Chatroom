import java.io.*;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            writer.println(createMessage(msg));
        } while(!msg.equals("/exit"));

        // check successfully quit
        writer.println(createByeMessage());
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
            String Msg = createRegisterMessage(name);
            System.out.println(Msg);
            writer.println(Msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Registering...");
        sleep(1000);
    }

    public String createRegisterMessage(String msg) {
        Message message = new Message();
        message.sender = clientInfo.getName();
        message.receivers = new String[]{};
        message.content = msg;
        message.messageType = MessageType.REGISTER;
        return message.getJsonString();
    }

    public String createByeMessage() {
        Message message = new Message();
        message.sender = clientInfo.getName();
        message.receivers = new String[]{};
        message.content = "";
        message.messageType = MessageType.QUIT;
        return message.getJsonString();
    }

    public String createMessage(String msg) {

        // TODO: Message parser

        Message message = new Message();
        message.sender = clientInfo.getName();
        String receivers[] = new String[1];
        if(getReceiver(msg) != null)
            receivers[0] = getReceiver(msg);
        message.receivers = receivers;
        message.content = getMsgString(msg);
        message.messageType = checkMessageType(msg);
        return message.getJsonString();
    }

    public String getReceiver(String msg) {
        String pattern = "/w\\s(.+)\\s+(.*)";
        Matcher matcher = Pattern.compile(pattern).matcher(msg);
        if(matcher.find()) {
            return matcher.group(1);
        } else {
            return "EVERYONE";
        }
    }

    public MessageType checkMessageType(String msg) {
        //
        String pattern = "/w\\s(.+)\\s+(.*)";
        Matcher matcher = Pattern.compile(pattern).matcher(msg);
        if(matcher.find()) {
            return MessageType.WHISPER;
        } else {
            return MessageType.GLOBAL;
        }
    }

    public String getMsgString(String msg) {
        String pattern = "/w\\s(.+)\\s+(.*)";
        Matcher matcher = Pattern.compile(pattern).matcher(msg);
        if(matcher.find()) {
            return matcher.group(2);
        } else {
            return msg;
        }
    }

}
