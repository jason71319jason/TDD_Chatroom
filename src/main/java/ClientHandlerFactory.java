import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ClientHandlerFactory {
    public ClientWriter createClientWriter(Client client) throws IOException {
        return new ClientWriter(client,
                new PrintWriter(
                        client.getSocket().getOutputStream(), true));
    }

    public ClientReader createClientReader(Client client) throws IOException {
        return new ClientReader(client,
                new BufferedReader(
                        new InputStreamReader(
                                client.getSocket()
                                        .getInputStream())));
    }
}
