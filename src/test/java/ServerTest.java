import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ServerTest {

    @Test
    public void server_defaultPort() {
        Server server = new Server();
        Assert.assertEquals(server.getPort(), Server.DEFAULT_PORT);
    }

    @Test
    public void server_givenNonOccupiedPort() {
        int givenPort = 54321;
        Server server = new Server(givenPort);
        Assert.assertEquals(server.getPort(), givenPort);
    }

    @Test (expected = IllegalArgumentException.class)
    public void server_givenInvalidPort() {
        int invalidPort = -1;
        Server server = new Server(invalidPort);
    }

    @Test (expected = IOException.class)
    public void run_OccupiedPort() {
        Server server = new Server();
        server.run();
        Server error_server = new Server();
        error_server.run();
    }

    @Test
    public void run_defaultPort() {
        Server server = new Server();
        server.run();
        Assert.assertEquals(server.getStatus(), Status.ACTIVE);
    }

    @Test
    public void run_givenPort() {
        Server server = new Server();
        server.run();
        Assert.assertEquals(server.getStatus(), Status.ACTIVE);
    }

    @Test
    public void shutdown_success() {
        Server server = new Server();
        server.shutdown();
        Assert.assertEquals(server.getStatus(), Status.INACTIVE);
    }
}