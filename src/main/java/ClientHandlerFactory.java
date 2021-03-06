import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ClientHandlerFactory {
    public ClientWriter createClientWriter(Client client) throws Exception {
        return new ClientWriter(client,
                new PrintWriter(
                        client.getSocket().getOutputStream(), true));
    }

    public ClientReader createClientReader(Client client) throws Exception {
        return new ClientReader(client,
                new BufferedReader(
                        new InputStreamReader(
                                client.getSocket()
                                        .getInputStream())));
    }

    public ClientRegister createClientRegister(Client client) throws Exception {
       return new ClientRegister(client,
               new PrintWriter(
                       client.getSocket().getOutputStream(), true),
               new BufferedReader(
                       new InputStreamReader(
                               client.getSocket().getInputStream())));
    }
}
