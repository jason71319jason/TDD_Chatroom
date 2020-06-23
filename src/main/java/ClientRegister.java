import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ClientRegister {

    private Client client;
    private PrintWriter writer;
    private BufferedReader socketReader;
    private BufferedReader systemReader;
    public ClientRegister(Client client,
                          PrintWriter writer,
                          BufferedReader reader) {
        this.client = client;
        this.writer = writer;
        this.socketReader = reader;
        this.systemReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public void register() {
        String name;
        String msg;
        String response;

        while(this.client.getStatus() == Status.ACTIVE &&
                this.client.getClientInfo().getName().isEmpty()) {
            try {

                // Input name
                System.out.print("Type your name: ");
                name = systemReader.readLine();

                // Check valid
                if(name.isEmpty()) {
                    System.out.println("Name can not be empty");
                    continue;
                }

                // register to server
                msg = createRegisterMessage(name);
                this.client.getLogger().info(msg);
                writer.println(msg);

                // receive server response
                response = socketReader.readLine();
                registerHandler(response);

            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void registerHandler(String msg) {
        this.client.getLogger().info(msg);

        Message receivedMessage = new Message();
        receivedMessage.setMessageByJson(new JSONObject(msg));
        if (receivedMessage.getMessageType() == MessageType.SERVER) {
            if (receivedMessage.content.equals("REGISTER_OK")) {
                // set client username
                this.client.getClientInfo().setName(receivedMessage.
                        getReceivers()[0]);
            } else if (receivedMessage.content.equals("REGISTER_FAILED")) {
                System.err.println("Register failed, the name had been used by others.");
            }
        } else {
            this.client.getLogger().warning("Error: Client receive " +
                    receivedMessage.getMessageType() + " type message");
        }
    }

    public String createRegisterMessage(String msg) {
        Message message = new Message();
        message.setMessage(this.client.getClientInfo().getName(),
                new String[]{},
                MessageType.REGISTER,
                msg);
        return message.getJsonString();
    }
}
