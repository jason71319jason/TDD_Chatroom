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
    public void setUp() throws Exception  {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        /* check if server exist, then shutdown it */
        server = null;
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
        when(mockServerHandlerFactory.createServerHandler(any(), any()))
                .thenReturn(mockServerHandler);
        doNothing().when(mockServerHandler).run();

        server = new Server(mockServerSocket, new ArrayList(), mockServerHandlerFactory);
        server.start();

        verify(mockServerHandlerFactory, times(3)).createServerHandler(any(), any());
        Assert.assertEquals(3, this.server.getServerHandles().size());
    }

    @Test (expected = IOException.class)
    public void start_acceptError() throws IOException {
        when(mockServerSocket.accept()).thenThrow(IOException.class);
        server = new Server(mockServerSocket, new ArrayList(), mockServerHandlerFactory);
        server.start();
    }

    @Test
    public void shutdown_success() throws IOException {
        server = new Server(mockServerSocket, new ArrayList(), mockServerHandlerFactory);
        server.shutdown();
        Assert.assertEquals(Status.INACTIVE, server.getStatus());
        Assert.assertEquals(0, this.server.getServerHandles().size());
    }

    @Test (expected = IOException.class)
    public void shutdown_closeSocketError() throws IOException {
        doThrow(new IOException()).when(mockServerSocket).close();
        server = new Server(mockServerSocket, new ArrayList(), mockServerHandlerFactory);
        server.shutdown();
    }

    @Test
    public void getStatus() throws IOException {
        when(mockServerSocket.accept()).thenReturn(null);

        server = new Server(mockServerSocket, new ArrayList(), mockServerHandlerFactory);
        Assert.assertEquals(Status.INACTIVE, server.getStatus());
        server.start();
        Assert.assertEquals(Status.ACTIVE, server.getStatus());
        server.shutdown();
        Assert.assertEquals(Status.INACTIVE, server.getStatus());
    }
}