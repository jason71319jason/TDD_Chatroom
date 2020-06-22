import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandlerFactory {
    public ClientWriter createClientWriter(Client client) {
        return new ClientWriter(client);
    }

    public ClientReader createClientReader(Client client) throws IOException {
        return new ClientReader(client,
                new BufferedReader(
                        new InputStreamReader(
                                client.getSocket()
                                        .getInputStream())));
    }
}
