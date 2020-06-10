import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.junit.Assert.*;

public class ClientTest {

    @Test
    public void connect_success() throws IOException {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo(username);
        Client client = new Client(clientInfo);

        client.connect(Server.DEFAULT_HOSTNAME, Server.DEFAULT_PORT);
        Assert.assertEquals(client.getStatus(), Status.ACTIVE);
    }

    @Test
    public void getStatus_correct() throws IOException {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo(username);
        Client client = new Client(clientInfo);

        Assert.assertEquals(client.getStatus(), Status.INACTIVE);

        client.connect(Server.DEFAULT_HOSTNAME, Server.DEFAULT_PORT);
        Assert.assertEquals(client.getStatus(), Status.ACTIVE);
    }

    @Test
    public void duplicate_name_failed() throws  IOException {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo(username);
        Client client = new Client(clientInfo);

        client.connect(Server.DEFAULT_HOSTNAME, Server.DEFAULT_PORT);

        ClientInfo clientInfo_2 = new ClientInfo(username);
        Client client_2 = new Client(clientInfo_2);

        client_2.connect(Server.DEFAULT_HOSTNAME, Server.DEFAULT_PORT);
        Assert.assertEquals(client.getStatus(), Status.INACTIVE);
    }

    @Test
    public void duplicate_name_failed() throws  IOException {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo(username);
        Client client = new Client(clientInfo);

        client.connect(Server.DEFAULT_HOSTNAME, Server.DEFAULT_PORT);
        Assert.assertEquals(client.getStatus(), Status.ACTIVE);

        client.leave();
        Assert.assertEquals(client.getStatus(), Status.INACTIVE);
    }

    @Test
    public void received_message_success() throws IOException {
        String username = "a";
        ClientInfo clientInfo = new ClientInfo(username);
        Client client = new Client(clientInfo);
        client.connect(Server.DEFAULT_HOSTNAME, Server.DEFAULT_PORT);

        String username_2 = "b";
        ClientInfo clientInfo_2 = new ClientInfo(username_2);
        Client client_2 = new Client(clientInfo_2);
        client_2.connect(Server.DEFAULT_HOSTNAME, Server.DEFAULT_PORT);

        String msg = "Hello\n";
        client.send(msg);
        assertEquals(msg, client_2.received());
    }

}