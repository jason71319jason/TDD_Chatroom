import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.*;

public class Server extends Thread {

    public static final int DEFAULT_PORT = 54321;
    public static Set<ServerHandler> serverHandlers;
    public static Logger logger;
    private Status status;
    private ServerSocket serverSocket;

    /**
     * Server constructor
     */
    public Server(ServerSocket socket, Set handlerSet, Logger logger) throws IOException {

        this.logger = logger;
        this.serverSocket = socket;
        this.serverHandlers = handlerSet;
        this.initLogger();

        serverSocket = new ServerSocket(Server.DEFAULT_PORT);
        serverHandlers = new HashSet<>();

        this.setStatus(Status.INACTIVE);
    }

    /**
     * Server constructor
     * @param port given binding port
     */
    public Server(int port) throws IOException {

        this.initLogger();

        serverSocket = new ServerSocket(port);
        serverHandlers = new HashSet<>();

        this.setStatus(Status.INACTIVE);
    }

    /**
     * Run server
     * Accept new client and create new thread to handle client
     */
    public void run() {

        try {
            Server.logger.info("Wait for new client");
            this.setStatus(Status.ACTIVE);
            while (this.status == Status.ACTIVE) {

                Socket clientSocket = serverSocket.accept();

                Server.logger.info("New client was accepted");
                Server.logger.info(String.format("IP: %s, Port: %d %n",
                        clientSocket.getInetAddress().toString(), clientSocket.getPort()));

                this.runServerHandler(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.shutdown();
        }
    }

    /**
     * Shutdown server
     */
    public void shutdown() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setStatus(Status.INACTIVE);
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

    /**
     * Get number of server handlers (client)
     * @return number of server handlers
     */
    public int getClientNum() {
        return serverHandlers.size();
    }

    /**
     * Set status of server
     * @param status status of server
     */
    private void setStatus(Status status) {
        this.status = status;
    }

    /**
     * initialize server logger
     */
    private void initLogger() {
        logger = Logger.getLogger("Chat Room Server");
        logger.setLevel(Level.ALL);
    }

    /**
     * Start thread of server handler
     * @param socket socket of client
     */
    private void runServerHandler(Socket socket) {
        ServerHandler newClientHandler = new ServerHandler(socket, this);
        this.serverHandlers.add(newClientHandler);
        newClientHandler.start();
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
