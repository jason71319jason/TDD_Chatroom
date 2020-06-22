import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientWriter extends Thread {

    private PrintWriter writer;
    private BufferedReader reader;
    private Client client;

    public ClientWriter(Client client, PrintWriter printWriter) {
        this.client = client;
        this.writer = printWriter;
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        String msg = null;
        // Register
        while(this.client.getClientInfo().getName().isEmpty()) {
            try {
                register();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Communication
        do {
            try {
                // prompt
                System.out.print("[" + this.client.getClientInfo().getName() + "]: ");
                msg = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.println(createMessage(msg));
        } while(!msg.equals("/exit"));

        // check successfully quit
        writer.println(createByeMessage());
        try {
            this.client.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register() throws InterruptedException {
        System.out.print("Type your name: ");
        String name;
        String msg;
        try {
            name = reader.readLine();
            if (name == null || name == "") {
                System.out.println("name can not be empty");
                return;
            }
            msg = createRegisterMessage(name);
            this.client.getLogger().info(msg);
            writer.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Registering...");

        // Dummy waiting
        sleep(1000);
    }

    public String createRegisterMessage(String msg) {
        Message message = new Message();
        message.setMessage(this.client.getClientInfo().getName(),
                new String[]{},
                MessageType.REGISTER,
                msg);
        return message.getJsonString();
    }

    public String createByeMessage() {
        Message message = new Message();
        message.setMessage(this.client.getClientInfo().getName(),
                new String[]{},
                MessageType.QUIT,
                "");
        return message.getJsonString();
    }

    public String createMessage(String msg) {
        Message message = new Message();
        message.setMessageByPlainText(msg);
        message.sender = this.client.getClientInfo().getName();
        return message.getJsonString();
    }
}
