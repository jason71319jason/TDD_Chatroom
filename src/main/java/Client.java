import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static Logger logger;

    private ClientInfo clientInfo;
    private Socket socket;
    private String hostname;
    private int port;

    /**
     *
     */
    public Client(String hostname, int port) throws IOException {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     *
     */
    public void connect() {
        try {
            socket = new Socket(hostname, port);
            logger.info("Successful connection");
            System.out.println("Loading ...");

            new ClientWriter(socket, clientInfo).start();
            new ClientReader(socket, clientInfo).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void register() {
        System.out.print("Type your name: ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            // TODO: Input check
            String name = reader.readLine();
            clientInfo = new ClientInfo(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) {
        Client client;
        logger = Logger.getLogger("Client");
        logger.setLevel(Level.ALL);
        try {
            client = new Client("localhost",12345);
            client.register();
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
