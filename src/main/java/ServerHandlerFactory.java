import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandlerFactory {
    public ServerHandler createServerHandler(Socket socket, Server server) throws IOException {
        return new ServerHandler(socket, server,
                new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream())),
                new PrintWriter(
                        socket.getOutputStream(), true));
    }
}
