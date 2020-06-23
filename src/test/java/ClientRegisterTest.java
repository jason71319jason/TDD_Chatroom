import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.util.Scanner;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ClientRegisterTest {

    @InjectMocks
    private ClientRegister clientRegister;
    @Mock
    private Client mockClient;
    @Mock
    private PrintWriter mockPrintWriter;
    @Mock
    private BufferedReader mockBufferedReader;
    @Mock
    private ClientInfo mockClientInfo;
    @Mock
    private Logger mockLogger;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        clientRegister = null;
        mockClient = null;
        mockPrintWriter = null;
        mockBufferedReader = null;
        mockClientInfo = null;
        mockLogger = null;
    }

    @Test
    public void register_success() throws IOException {
        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClient.getStatus())
                .thenReturn(Status.ACTIVE)
                .thenReturn(Status.INACTIVE);
        when(mockClientInfo.getName()).thenReturn("");
        when(mockClient.getLogger()).thenReturn(mockLogger);
        when(mockBufferedReader.readLine()).thenReturn(null);

        String name = "A";
        System.setIn(new ByteArrayInputStream(name.getBytes()));
        System.setIn(System.in);

        clientRegister = new ClientRegister(mockClient,
                mockPrintWriter,
                mockBufferedReader);
        clientRegister.register();

        String expected = "{\"messageType\":\"REGISTER\",\"sender\":\"\",\"receivers\":[],\"content\":\"A\"}";
        verify(mockPrintWriter, times(1))
                .println(expected);
    }
    @Test
    public void register_emptyName() {
        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClient.getStatus())
                .thenReturn(Status.ACTIVE)
                .thenReturn(Status.INACTIVE);
        when(mockClientInfo.getName()).thenReturn("");
        when(mockClient.getLogger()).thenReturn(mockLogger);

        Message message = new Message();
        message.setMessage("SERVER",
                new String[]{},
                MessageType.SERVER,
                "REGISTER_FAILED");

        String name = "\n";
        System.setIn(new ByteArrayInputStream(name.getBytes()));

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));

        clientRegister = new ClientRegister(mockClient,
                mockPrintWriter,
                mockBufferedReader);
        clientRegister.register();

        System.setIn(System.in);
        System.setOut(System.out);

        Assert.assertEquals("Type your name: Name can not be empty\n", byteArrayOutputStream.toString());
    }

    @Test
    public void register_duplicateName() throws IOException {
        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClient.getStatus())
                .thenReturn(Status.ACTIVE)
                .thenReturn(Status.ACTIVE)
                .thenReturn(Status.ACTIVE)
                .thenReturn(Status.INACTIVE);
        when(mockClientInfo.getName()).thenReturn("");
        when(mockClient.getLogger()).thenReturn(mockLogger);

        Message message = new Message();
        message.setMessage("SERVER",
                new String[]{},
                MessageType.SERVER,
                "REGISTER_FAILED");

        when(mockBufferedReader.readLine())
                .thenReturn(message.getJsonString())
                .thenReturn(message.getJsonString())
                .thenReturn(null);

        String name = "A\nA\nB\n";
        System.setIn(new ByteArrayInputStream(name.getBytes()));
        System.setIn(System.in);

        clientRegister = new ClientRegister(mockClient,
                mockPrintWriter,
                mockBufferedReader);
        clientRegister.register();

        String expected = "{\"messageType\":\"REGISTER\",\"sender\":\"\",\"receivers\":[],\"content\":\"A\"}";
        verify(mockPrintWriter, times(2))
                .println(expected);
        expected = "{\"messageType\":\"REGISTER\",\"sender\":\"\",\"receivers\":[],\"content\":\"B\"}";
        verify(mockPrintWriter, times(1))
                .println(expected);
    }

    @Test
    public void registerHandler_registerOK() {
        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClientInfo.getName()).thenReturn("");
        when(mockClient.getLogger()).thenReturn(mockLogger);

        clientRegister = new ClientRegister(mockClient,
                mockPrintWriter,
                mockBufferedReader);

        Message message = new Message();
        message.setMessage("SERVER",
                new String[]{"A"},
                MessageType.SERVER,
                "REGISTER_OK");

        clientRegister.registerHandler(message.getJsonString());

        verify(mockClientInfo, times(1)).setName("A");
    }

    @Test
    public void registerHandler_registerFailed() throws IOException {

        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClientInfo.getName()).thenReturn("");
        when(mockClient.getLogger()).thenReturn(mockLogger);

        clientRegister = new ClientRegister(mockClient,
                mockPrintWriter,
                mockBufferedReader);

        Message message = new Message();
        message.setMessage("SERVER",
                new String[]{},
                MessageType.SERVER,
                "REGISTER_FAILED");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(byteArrayOutputStream));

        clientRegister.registerHandler(message.getJsonString());

        byteArrayOutputStream.flush();
        String result = new String(byteArrayOutputStream.toByteArray());
        System.setOut(System.out);

        Assert.assertEquals("Register failed, the name had been used by others.\n",
                result);

        verify(mockClientInfo, times(0)).setName(anyString());
    }

    @Test
    public void registerHandler_registerError() {

        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClientInfo.getName()).thenReturn("");
        when(mockClient.getLogger()).thenReturn(mockLogger);

        clientRegister = new ClientRegister(mockClient,
                mockPrintWriter,
                mockBufferedReader);

        Message message = new Message();
        message.setMessage("SERVER",
                new String[]{},
                MessageType.REGISTER,
                "REGISTER_FAILED");

        clientRegister.registerHandler(message.getJsonString());

        verify(mockLogger, times(1)).warning(anyString());
    }

    @Test
    public void createRegisterMessage_create() {

        when(mockClient.getClientInfo()).thenReturn(mockClientInfo);
        when(mockClientInfo.getName()).thenReturn("");

        clientRegister = new ClientRegister(mockClient,
                mockPrintWriter,
                mockBufferedReader);

        Message message = new Message();
        message.setMessage("",
                new String[]{},
                MessageType.REGISTER,
                "A");

        String result = clientRegister.createRegisterMessage("A");
        Assert.assertEquals(message.getJsonString(), result);
    }

}