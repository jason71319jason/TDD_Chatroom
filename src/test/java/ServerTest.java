import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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

    }

    // accept client register
    @Test
    public void acceptClientRegister() throws IOException {

    }

    // reject client register
    @Test
    public void rejectClientRegister() throws IOException {

    }

    // Broadcast work success
    @Test
    public void broadcast_success() throws IOException {

    }

    // Handle client list
    @Test
    public void handleClientList() throws IOException {

    }

    @Test
    public void shutdown_success() throws IOException {
        Server server = new Server();
        server.shutdown();
        Assert.assertEquals(server.getStatus(), Status.INACTIVE);
    }
}