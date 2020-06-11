//import jdk.vm.ci.code.site.Call;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static Logger logger;

    public static ClientInfo clientInfo;
    private Socket socket;
    private String hostname;
    private int port;
    private ClientWriter clientWrite;
    private ClientReader clientreader;
    private static Status status;
    /**
     *
     */
    public Client(ClientInfo info) throws IOException {
        clientInfo = info;
    }

    /**
     *
     */
    public void connect(ServerInfo serverInfo) {
        try {
            socket = new Socket(serverInfo.getHostname(), serverInfo.getPort());
            status = Status.ACTIVE;
            new ClientWriter(socket, clientInfo, this).start();
            new ClientReader(socket, clientInfo, this).start();

        } catch (IOException e) {
            status = Status.INACTIVE;
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void disconnect() {
        try {
            socket.close();
            status = Status.INACTIVE;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Status getStatus() {
        return status;
    }


    /**
     *
     */
//    public void register(Callback callback) {
//        System.out.print("Type your name: ");
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        try {
//
//            socket = new Socket(hostname, port);
//            logger.info("Successful connection");
//            System.out.println("Loading ...");
//
//            new ClientWriter(socket, clientInfo).start();
//            new ClientReader(socket, clientInfo).start();
//
            // TODO: Input check
//            String name = reader.readLine();
//            BufferedOutputStream out = new BufferedOutputStream(socket
//                    .getOutputStream());
//            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            out.write(name.getBytes());
//            clientInfo = new ClientInfo();
//            return ;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String argv[]) throws IOException {
        status = Status.INACTIVE;
        clientInfo = new ClientInfo();
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setHostname("localhost");
        serverInfo.setPort(12345);
        Client client;
        Callback callback = new Callback() {
            @Override
            public void callback(String response) {
                System.out.println(response);
            }
        };
        logger = Logger.getLogger("Client");
        logger.setLevel(Level.ALL);
        client = new Client(clientInfo);
        client.connect(serverInfo);
    }
}

interface Callback {
    public void callback(String response);
}

