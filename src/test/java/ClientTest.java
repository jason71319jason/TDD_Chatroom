import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.junit.Assert.*;

public class ClientTest {
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
        /* if server exist, then shutdown it */
        // server.shutdown();
    }

    // Connect success
    @Test
    public void connect_success() throws IOException {
        client = new Client(clientInfo);
        client.connect(serverInfo);
        Assert.assertEquals(Status.ACTIVE, client.getStatus());
    }

    // Connect failed
    @Test
    public void connect_failed() throws IOException {
        serverInfo.setHostname("0");
        client = new Client(clientInfo);
        client.connect(serverInfo);
        Assert.assertEquals(Status.INACTIVE, client.getStatus());
    }

    // Register success
    @Test
    public void getClientStatus_correct() throws IOException {
        client = new Client(clientInfo);

        Assert.assertEquals(Status.INACTIVE, client.getStatus());
        client.connect(serverInfo);
        Assert.assertEquals(Status.ACTIVE, client.getStatus());
    }

    // Register failed
    @Test
    public void duplicateName_failed() throws  IOException {
        client = new Client(clientInfo);

        client.connect(serverInfo);

        ClientInfo clientInfo_2 = new ClientInfo();
        Client client_2 = new Client(clientInfo_2);

        client_2.connect(serverInfo);
        Assert.assertEquals(Status.INACTIVE, client.getStatus());
    }

    // Send Message success
    @Test
    public void sendMessage_success() throws IOException {
        ClientInfo clientInfo_1 = new ClientInfo();
        clientInfo_1.setName("a");
        Client client_1 = new Client(clientInfo_1);
        client_1.connect(serverInfo);

        ClientInfo clientInfo_2 = new ClientInfo();
        clientInfo_2.setName("b");
        Client client_2 = new Client(clientInfo_2);
        client_2.connect(serverInfo);

        String msg = "Hello\n";
        // this part should implement by read/write buffer
//        client.send(msg);
//        assertEquals(msg, client_2.received());
    }

    // Received Message success
    @Test
    public void received_message_success() throws IOException {
        String username = "a";
        ClientInfo clientInfo = new ClientInfo();
        Client client = new Client(clientInfo);
        client.connect(serverInfo);

        String username_2 = "b";
        ClientInfo clientInfo_2 = new ClientInfo();
        Client client_2 = new Client(clientInfo);
        client_2.connect(serverInfo);

        String msg = "Hello\n";
        // this part should implement by read/write buffer
//        client.send(msg);
//        assertEquals(msg, client_2.received());
    }

    // Client leave server success
    @Test
    public void clientLeaveStatus_correct() throws  IOException {
        client = new Client(clientInfo);

        client.connect(serverInfo);
        Assert.assertEquals(Status.ACTIVE, client.getStatus());

        client.disconnect();
        Assert.assertEquals(Status.INACTIVE, client.getStatus());
    }

    // Server crash
    @Test
    public void clientStatusWithServerCrash() throws IOException {
        client  = new Client(clientInfo);

        client.connect(serverInfo);
        Assert.assertEquals(Status.ACTIVE, client.getStatus());

        server.shutdown();
        Assert.assertEquals(Status.INACTIVE, server.getStatus());
        Assert.assertEquals(Status.INACTIVE, client.getStatus());
    }

}