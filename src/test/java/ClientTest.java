import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class ClientTest {

    @InjectMocks
    private Client client;

    @Mock
    private Socket mockSocket;
    @Mock
    private ClientInfo mockClientInfo;
    @Mock
    private ClientHandlerFactory mockClientHandlerFactory;
    @Mock
    private ClientReader mockClientReader;
    @Mock
    private ClientWriter mockClientWriter;
    @Mock
    private ClientRegister mockClientRegister;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        mockSocket = null;
        mockClientInfo = null;
        mockClientHandlerFactory = null;
        mockClientReader = null;
        mockClientWriter = null;
        mockClientRegister = null;
    }

    // Connect success
    @Test
    public void connect_success() throws IOException {
        when(mockClientHandlerFactory.createClientReader(any()))
                .thenReturn(mockClientReader);
        when(mockClientHandlerFactory.createClientWriter(any()))
                .thenReturn(mockClientWriter);
        when(mockClientHandlerFactory.createClientRegister(any()))
                .thenReturn(mockClientRegister);

        client = new Client(mockSocket, mockClientInfo,
                mockClientHandlerFactory);

        client.connect();
        Assert.assertEquals(Status.ACTIVE, client.getStatus());
    }

    // Client leave server success
    @Test
    public void disconnect_correct() throws  IOException {
        when(mockClientHandlerFactory.createClientReader(any()))
                .thenReturn(mockClientReader);
        when(mockClientHandlerFactory.createClientWriter(any()))
                .thenReturn(mockClientWriter);
        when(mockClientHandlerFactory.createClientRegister(any()))
                .thenReturn(mockClientRegister);

        client = new Client(mockSocket, mockClientInfo,
                mockClientHandlerFactory);

        client.connect();
        Assert.assertEquals(Status.ACTIVE, client.getStatus());
        client.disconnect();
        Assert.assertEquals(Status.INACTIVE, client.getStatus());
    }

}