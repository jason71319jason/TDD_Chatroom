import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;

import static org.junit.Assert.*;

public class ClientTest {
    ServerInfo serverInfo = new ServerInfo();
    @Before
    public void setServerInfo(){
        serverInfo.setHostname("localhost");
        serverInfo.setPort(12345);
    }

    @Test
    public void connect_success() throws IOException {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setName(username);
        Client client = new Client(clientInfo);

        client.connect(serverInfo);
        Assert.assertEquals(Status.ACTIVE, client.getStatus());
    }

    @Test
    public void getClientStatus_correct() throws IOException {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo();
        Client client = new Client(clientInfo);

        Assert.assertEquals(Status.INACTIVE, client.getStatus());

        client.connect(serverInfo);
        Assert.assertEquals(Status.ACTIVE, client.getStatus());
    }

    @Test
    public void duplicateName_failed() throws  IOException {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo();
        Client client = new Client(clientInfo);

        client.connect(serverInfo);

        ClientInfo clientInfo_2 = new ClientInfo();
        Client client_2 = new Client(clientInfo_2);

        client_2.connect(serverInfo);
        Assert.assertEquals(Status.INACTIVE, client.getStatus());
    }

    @Test
    public void clientLeaveStatus_correct() throws  IOException {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo();
        Client client = new Client(clientInfo);

        client.connect(serverInfo);
        Assert.assertEquals(Status.ACTIVE, client.getStatus());

        client.disconnect();
        Assert.assertEquals(Status.INACTIVE, client.getStatus());
    }

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

}