import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ServerTest {

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
        Assert.assertEquals(Status.ACTIVE, server.getStatus());
    }

    @Test
    public void run_givenPort() throws IOException {
        Server server = new Server();
        server.run();
        Assert.assertEquals(Status.ACTIVE, server.getStatus());
    }

    @Test
    public void shutdown_success() throws IOException {
        Server server = new Server();
        server.shutdown();
        Assert.assertEquals(Status.INACTIVE, server.getStatus());
    }
}