import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.*;

public class Server extends Thread {

    public static final int DEFAULT_PORT = 12345;
    public static Set<ServerHandler> serverHandlers;
    public static Logger logger;
    private Status status;
    private ServerSocket serverSocket;

    /**
     * Server constructor
     */
    public Server() throws IOException {
        logger = Logger.getLogger("Chat Room Server");
        logger.setLevel(Level.ALL);
        serverSocket = new ServerSocket(Server.DEFAULT_PORT);
        serverHandlers = new HashSet<>();
        status = Status.INACTIVE;
    }

    /**
     * Server constructor
     * @param port binding port
     */
    public Server(int port) throws IOException {
        logger = Logger.getLogger("Chat Room Server");
        logger.setLevel(Level.ALL);
        serverSocket = new ServerSocket(port);
        serverHandlers = new HashSet<>();
        status = Status.INACTIVE;
    }


    /**
     * Run server
     */
    public void run() {

        try {
            logger.info("Wait for new client");

            while (true) {

                Socket clientSocket = serverSocket.accept();

                logger.info("New client was accepted");
                logger.info(String.format("IP: %s, Port: %d %n",
                        clientSocket.getInetAddress().toString(), clientSocket.getPort()));

                ServerHandler newClientHandler = new ServerHandler(clientSocket, this);
                serverHandlers.add(newClientHandler);
                newClientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shutdown server
     */
    public void shutdown() {
        
    }

    /**
     * Get server's status
     * @return server's status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Get server's binding port
     * @return server's binding port
     */
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public int getClientNum() {
        return serverHandlers.size();
    }

    public static void main(String argv[]) {
        try {
            Server server = new Server(12345);
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
