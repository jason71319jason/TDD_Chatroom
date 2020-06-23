import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClientReaderTest {

    @InjectMocks
    private ClientReader clientReader;
    @Mock
    private Client mockClient;
    @Mock
    private BufferedReader mockBufferedReader;
    @Mock
    private Logger mockLogger;
    @Mock
    private ClientInfo mockClientInfo;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        clientReader = null;
        mockClient = null;
        mockBufferedReader = null;
        mockLogger = null;
        mockClientInfo = null;
    }

    @Test
    public void responseHandler_whisper() throws IOException {
        when(mockClient.getLogger()).thenReturn(mockLogger);
        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClientInfo.getName()).thenReturn("A");
        clientReader = new ClientReader(mockClient, mockBufferedReader);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));

        Message message = new Message();
        message.setMessage("A",new String[]{"B"},
                MessageType.WHISPER, "A whispers to B");
        clientReader.responseHandler(message.getJsonString());

        byteArrayOutputStream.flush();
        String result = new String(byteArrayOutputStream.toByteArray());

        System.setOut(System.out);
        Assert.assertEquals(result, "A whispers to B\n");
    }

    @Test
    public void responseHandler_broadcast() throws IOException {
        when(mockClient.getLogger()).thenReturn(mockLogger);
        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClientInfo.getName()).thenReturn("A");
        clientReader = new ClientReader(mockClient, mockBufferedReader);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));

        Message message = new Message();
        message.setMessage("A",new String[]{},
                MessageType.GLOBAL, "A broadcasts");
        clientReader.responseHandler(message.getJsonString());

        byteArrayOutputStream.flush();
        String result = new String(byteArrayOutputStream.toByteArray());

        System.setOut(System.out);
        Assert.assertEquals(result, "A broadcasts\n");
    }

    @Test
    public void responseHandler_RegisterFail() throws IOException {
        when(mockClient.getLogger()).thenReturn(mockLogger);
        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClientInfo.getName()).thenReturn("Test man");
        clientReader = new ClientReader(mockClient, mockBufferedReader);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setErr(new PrintStream(byteArrayOutputStream));

        Message message = new Message();
        message.setMessage("SERVER_NAME",new String[]{"Test man"},
                MessageType.SERVER, "REGISTER_FAILED");
        clientReader.responseHandler(message.getJsonString());

        byteArrayOutputStream.flush();
        String result = new String(byteArrayOutputStream.toByteArray());

        System.setErr(System.err);
        verify(mockClientInfo, times(0)).setName("Register failed, the name had been used by others.");
    }

}