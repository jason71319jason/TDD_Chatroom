import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientInfoTest {

    @Test (expected = NullPointerException.class)
    public void ClientInfo_null() {
        ClientInfo clientInfo = new ClientInfo(null);
        Assert.assertNull(clientInfo.getName());
    }

    @Test
    public void getName_nonNull() {
        String username = "Eric";
        ClientInfo clientInfo = new ClientInfo(username);
        Assert.assertNotNull(clientInfo.getName());
    }

    @Test
    public void userName_correct() {
        String username = "abcd";
        ClientInfo clientInfo = new ClientInfo(username);
        Assert.assertEquals(username, clientInfo.getName());
    }
}