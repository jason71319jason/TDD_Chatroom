import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.Mockito.*;

public class ServerTest {

    @InjectMocks
    private Server server;

    @Mock
    private ServerSocket mockServerSocket;
    @Mock
    private Socket mockClientSocket;
    @Mock
    private ServerHandlerFactory mockServerHandlerFactory;
    @Mock
    private ServerHandler mockServerHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void shutdownServer() {
        /* check if server exist, then shutdown it */
        server.shutdown();
        mockServerSocket = null;
        mockClientSocket = null;
    }

    @Test
    public void getPort_defaultPort() {

        when(mockServerSocket.getLocalPort()).thenReturn(Server.DEFAULT_PORT);
        this.server = new Server(mockServerSocket, new ArrayList(), mockServerHandlerFactory);
        Assert.assertEquals(Server.DEFAULT_PORT, this.server.getPort());
    }

    @Test
    public void start_acceptConnection() throws IOException {
        when(mockServerSocket.accept())
                .thenReturn(mockClientSocket)
                .thenReturn(mockClientSocket)
                .thenReturn(mockClientSocket)
                .thenReturn(null);
        when(mockClientSocket.getInetAddress())
                .thenReturn(InetAddress.getByAddress(
                        new byte[]{(byte) 192, (byte) 168, (byte) 132, (byte)1}))
                .thenReturn(InetAddress.getByAddress(
                        new byte[]{(byte) 192, (byte) 168, (byte) 132, (byte)2}))
                .thenReturn(InetAddress.getByAddress(
                        new byte[]{(byte) 192, (byte) 168, (byte) 132, (byte)3}));
        when(mockClientSocket.getPort())
                .thenReturn(12345)
                .thenReturn(23456)
                .thenReturn(34567);
        when(mockServerHandlerFactory.createServerHandler(mockClientSocket))
                .thenReturn(mockServerHandler);

        server = new Server(mockServerSocket, new ArrayList(), mockServerHandlerFactory);
        server.start();

        verify(mockServerHandlerFactory, times(3)).createServerHandler(any());
        Assert.assertEquals(3, Server.serverHandlers.size());
    }

    @Test (expected = IOException.class)
    public void start_acceptError() throws IOException {
        when(mockServerSocket.accept()).thenThrow(IOException.class);
        Server server = new Server(mockServerSocket, new ArrayList(), mockServerHandlerFactory);
        server.start();
    }
}