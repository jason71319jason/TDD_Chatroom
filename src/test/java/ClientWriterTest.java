import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ClientWriterTest {

    @InjectMocks
    private ClientWriter clientWriter;
    @Mock
    private Client mockClient;
    @Mock
    private PrintWriter mockPrintWriter;
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
        clientWriter = null;
        mockClient = null;
        mockPrintWriter = null;
        mockLogger = null;
        mockClientInfo = null;
    }

    @Test
    public void register_inputSuccess() throws InterruptedException {
        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClientInfo.getName()).thenReturn("");
        when(mockClient.getLogger()).thenReturn(mockLogger);
        doNothing().when(mockPrintWriter).println(anyString());

        String name = "A";
        System.setIn(new ByteArrayInputStream(name.getBytes()));
        Scanner scanner = new Scanner(System.in);

        System.setIn(System.in);
        clientWriter = new ClientWriter(mockClient, mockPrintWriter);
        clientWriter.register();

        String expected = "{\"messageType\":\"REGISTER\",\"sender\":\"\",\"receivers\":[],\"content\":\"A\"}";
        verify(mockPrintWriter, times(1))
                .println(expected);
    }

    @Test
    public void createMessage_success() {
        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClientInfo.getName()).thenReturn("A");

        clientWriter = new ClientWriter(mockClient, mockPrintWriter);

        Message message = new Message();

        // Broadcast
        message.setMessage("A", new String[]{},
                MessageType.GLOBAL, "A broadcasts");

        String result = clientWriter.createMessage("A broadcasts");
        Assert.assertEquals(message.getJsonString(), result);

        // Whisper
        message.setMessage("A", new String[]{"B"},
                MessageType.WHISPER, "A whispers to B");

        result = clientWriter.createMessage("/w B A whispers to B");
        Assert.assertEquals(message.getJsonString(), result);

    }

    @Test
    public void createRegisterMessage_success() {
        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClientInfo.getName()).thenReturn("A");

        clientWriter = new ClientWriter(mockClient, mockPrintWriter);

        Message message = new Message();

        message.setMessage("A",
                new String[]{},
                MessageType.REGISTER,
                "A");

        String result = clientWriter.createRegisterMessage("A");
        Assert.assertEquals(message.getJsonString(), result);
    }

    @Test
    public void createByeMessage_success() {
        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClientInfo.getName()).thenReturn("A");

        clientWriter = new ClientWriter(mockClient, mockPrintWriter);

        Message message = new Message();

        message.setMessage("A",
                new String[]{},
                MessageType.QUIT,
                "");

        String result = clientWriter.createByeMessage();
        Assert.assertEquals(message.getJsonString(), result);
    }
}