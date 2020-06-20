import java.net.Socket;

public class ServerHandlerFactory {
    public ServerHandler createServerHandler(Socket socket) {
        ServerHandler serverHandler = new ServerHandler(socket);
        return serverHandler;
    }
}
