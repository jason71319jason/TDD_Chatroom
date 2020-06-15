import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientInfoTest {

    @Test
    public void ClientInfo_null() {
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setName(null);
        Assert.assertNull(clientInfo.getName());
    }

    @Test
    public void getName_nonNull() {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setName(username);
        Assert.assertNotNull(clientInfo.getName());
    }

    @Test
    public void userName_correct() {
        String username = "abcd";
        ClientInfo clientInfo = new ClientInfo();
        clientInfo.setName(username);
        Assert.assertEquals(username, clientInfo.getName());
    }
}