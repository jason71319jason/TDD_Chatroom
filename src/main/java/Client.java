//import jdk.vm.ci.code.site.Call;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Thread {

    public static final int SERVER_DEFAULT_PORT = 54321;
    private ClientInfo clientInfo;
    private Logger logger;
    private Status status;
    private Socket socket;
    private ClientHandlerFactory clientHandlerFactory;

    /**
     *
     */
    public Client(Socket socket, ClientInfo clientInfo,
                  ClientHandlerFactory clientHandlerFactory) {
        this.socket = socket;
        this.clientInfo = clientInfo;
        this.clientHandlerFactory = clientHandlerFactory;

        this.initLogger();
        this.setStatus(Status.INACTIVE);
    }

    private void setStatus(Status status) {
        this.status = status;
    }

    public void start() {

    }
    /**
     *
     */
    public void connect() throws IOException {
        this.status = Status.ACTIVE;
        this.clientHandlerFactory.createClientWriter(this).start();
        this.clientHandlerFactory.createClientReader(this).start();
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

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public Logger getLogger() {
        return logger;
    }
    /**
     * initialize server logger
     */
    private void initLogger() {
        logger = Logger.getLogger("Chat Room Client");
        logger.setLevel(Level.ALL);
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", Client.SERVER_DEFAULT_PORT);
            ClientInfo clientInfo = new ClientInfo();
            ClientHandlerFactory clientHandlerFactory = new ClientHandlerFactory();
            Client client = new Client(socket, clientInfo, clientHandlerFactory);
            client.connect();
        } catch  (IOException e) {
            e.printStackTrace();
        }
    }
}


