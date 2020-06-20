import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.*;

public class Server {

    public static final int DEFAULT_PORT = 54321;
    public static List<ServerHandler> serverHandlers;
    public static Logger logger;

    private Status status;
    private ServerSocket serverSocket;
    private ServerHandlerFactory serverHandlerFactory;

    /**
     * Server constructor
     */
    public Server(ServerSocket socket, List handlerList,
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
    public void start() throws IOException{

        Server.logger.info("Wait for new client");
        this.setStatus(Status.ACTIVE);
        Socket clientSocket;
        while((clientSocket = serverSocket.accept()) != null) {
            Server.logger.info("New client was accepted");
            Server.logger.info(String.format("IP: %s, Port: %d %n",
                    clientSocket.getInetAddress().toString(), clientSocket.getPort()));

            this.runServerHandler(clientSocket);
        }
    }

    /**
     * Shutdown server
     */
    public void shutdown() throws IOException {
        this.serverSocket.close();
        Server.serverHandlers.clear();
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
    private void runServerHandler(Socket socket) {
        ServerHandler handler = serverHandlerFactory.createServerHandler(socket);
        this.serverHandlers.add(handler);
        handler.start();
    }

    public static void main(String argv[]) {
        try {
            ServerSocket serverSocket = new ServerSocket(Server.DEFAULT_PORT);
            List handleList= new ArrayList();
            ServerHandlerFactory serverHandlerFactory = new ServerHandlerFactory();
            Server server = new Server(serverSocket, handleList, serverHandlerFactory);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
