import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ServerTest {
    private ServerInfo serverInfo;
    private Server server;
    private ClientInfo clientInfo;
    private Client client;
    String username = "Eric";

    @Before
    public void setServerInfo() throws IOException {
        clientInfo = new ClientInfo();
        clientInfo.setName(username);

        serverInfo = new ServerInfo();
        serverInfo.setHostname("localhost");
        serverInfo.setPort(12345);

        server = new Server();
        Thread serverThread = new Thread(server);
        serverThread.start();

    }

    @After
    public void shutdownServer() {
        /* check if server exist, then shutdown it */
         server.shutdown();
    }

    // Server start success
    @Test
    public void server_defaultPort() throws IOException {
        Server server = new Server();
        Assert.assertEquals(Server.DEFAULT_PORT, server.getPort());
    }

    @Test
    public void server_givenNonOccupiedPort() throws IOException {
        int givenPort = 54321;
        Server server = new Server(givenPort);
        Assert.assertEquals(givenPort, server.getPort());
    }

    // Server start failed
    @Test (expected = IllegalArgumentException.class)
    public void server_givenInvalidPort() throws IOException {
        int invalidPort = -1;
        Server server = new Server(invalidPort);
    }

    @Test (expected = IOException.class)
    public void run_OccupiedPort() throws IOException {
        Server server = new Server();
        server.run();
        Server error_server = new Server();
        error_server.run();
    }

    @Test
    public void run_defaultPort() throws IOException {
        Server server = new Server();
        server.run();
        Assert.assertEquals(server.getStatus(), Status.ACTIVE);
    }

    @Test
    public void run_givenPort() throws IOException {
        Server server = new Server();
        server.run();
        Assert.assertEquals(server.getStatus(), Status.ACTIVE);
    }


    // Server handle client connect
    @Test
    public void handleClientConnect() throws IOException {
        Server server = new Server();
        server.run();
        client = new Client(clientInfo);
        client.connect(serverInfo);
        Assert.assertEquals(1, server.getClientNum());
    }

    // accept client register
    @Test
    public void acceptClientRegister() throws IOException {
        Server server = new Server();
        server.run();
        client = new Client(clientInfo);
        clientInfo.setName("c");
        client.connect(serverInfo);
        /* How to assert? This is not enough. */
        Assert.assertEquals(1, server.getClientNum());
    }

    // reject client register
    @Test
    public void rejectClientRegister() throws IOException {
        Server server = new Server();
        server.run();

        ClientInfo clientInfo_1 = new ClientInfo();
        Client client_1 = new Client(clientInfo_1);
        clientInfo_1.setName("a");
        client_1.connect(serverInfo);

        ClientInfo clientInfo_2 = new ClientInfo();
        Client client_2 = new Client(clientInfo_2);
        clientInfo_2.setName("a");
        client_2.connect(serverInfo);

        Assert.assertEquals(1, server.getClientNum());
    }

    // Broadcast server message success
    @Test
    public void broadcast_success() throws IOException {
        Server server = new Server();
        server.run();

        ClientInfo clientInfo_1 = new ClientInfo();
        Client client_1 = new Client(clientInfo_1);
        clientInfo_1.setName("a");
        client_1.connect(serverInfo);

        ClientInfo clientInfo_2 = new ClientInfo();
        Client client_2 = new Client(clientInfo_2);
        clientInfo_2.setName("b");
        client_2.connect(serverInfo);

        client_1.disconnect();
        /* Use Mockito to Test
        * */
    }

    // Handle client list
    @Test
    public void handleClientList() throws IOException {
        Server server = new Server();
        server.run();
        Set<Client> clientSet = new HashSet<>();
        int MaxClientNumber = 20;
        for (int i = 1; i <= MaxClientNumber; i++) {
            ClientInfo clientInfo_loop = new ClientInfo();
            clientInfo_loop.setName(String.valueOf(i));
            Client client = new Client(clientInfo);
            client.connect(serverInfo);
            clientSet.add(client);
            Assert.assertEquals(i, server.getClientNum());
        }
    }

    @Test
    public void shutdown_success() throws IOException {
        Server server = new Server();
        server.shutdown();
        Assert.assertEquals(server.getStatus(), Status.INACTIVE);
    }
}