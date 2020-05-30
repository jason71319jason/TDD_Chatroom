import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;

public class ClientTest {

    @Test
    public void connect_success() {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo(username);
        Client client = new Client(clientInfo);

        client.connect(Server.DEFAULT_HOSTNAME, Server.DEFAULT_PORT);
        Assert.assertEquals(client.getStatus(), Status.ACTIVE);
    }

    @Test
    public void getStatus_correct() {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo(username);
        Client client = new Client(clientInfo);

        Assert.assertEquals(client.getStatus(), Status.INACTIVE);

        client.connect(Server.DEFAULT_HOSTNAME, Server.DEFAULT_PORT);
        Assert.assertEquals(client.getStatus(), Status.ACTIVE);
    }
}