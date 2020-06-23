import java.io.*;

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
        String msg = "";
        // Communication
        while(this.client.getStatus() == Status.ACTIVE &&
                !msg.equals("/exit")) {
            try {
                // prompt
                System.out.print("[" + this.client.getClientInfo().getName() + "]: ");
                msg = reader.readLine();
                writer.println(createMessage(msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // check successfully quit
        if(this.client.getStatus() == Status.ACTIVE)
            writer.println(createByeMessage());

        this.client.disconnect();
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
