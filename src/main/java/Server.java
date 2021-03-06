import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    public static final int DEFAULT_PORT = 54321;

    private Logger logger;
    private List<ServerHandler> serverHandlers;
    private Status status;
    private ServerSocket serverSocket;
    private ServerHandlerFactory serverHandlerFactory;

    /**
     * Server constructor
     */
    public Server(ServerSocket socket, List<ServerHandler> handlerList,
                  ServerHandlerFactory serverHandlerFactory) {
        this.serverSocket = socket;
        this.serverHandlers = handlerList;
        this.serverHandlerFactory = serverHandlerFactory;

        this.logger = Logger.getLogger("Chat room server");
        this.initLogger();

        this.setStatus(Status.INACTIVE);
    }

    /**
     * Run server
     * Accept new client and create new thread to handle client
     */
    public void start() throws IOException {

        this.logger.info("Wait for new client");
        this.setStatus(Status.ACTIVE);
        Socket clientSocket;
        while((clientSocket = serverSocket.accept()) != null && clientSocket.isConnected()) {
            this.logger.info("New client was accepted");
            this.logger.info(String.format("IP: %s, Port: %d %n",
                    clientSocket.getInetAddress().toString(), clientSocket.getPort()));

            this.runServerHandler(clientSocket);
        }
    }

    /**
     * Shutdown server
     */
    public void shutdown() throws IOException {
        this.serverSocket.close();
        this.serverHandlers.clear();
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

    public List<ServerHandler> getServerHandles() {
        return this.serverHandlers;
    }

    public Logger getLogger() {
        return this.logger;
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
     */
    private void runServerHandler(Socket socket) throws IOException {
        ServerHandler handler = serverHandlerFactory.createServerHandler(socket, this);
        handler.start();
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(Server.DEFAULT_PORT);
            List<ServerHandler> handleList= new ArrayList<>();
            ServerHandlerFactory serverHandlerFactory = new ServerHandlerFactory();
            Server server = new Server(serverSocket, handleList, serverHandlerFactory);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
