import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ServerHandlerTest {

    @Mock
    private Socket mockSocket;
    @Mock
    private BufferedReader mockBufferedReader;
    @Mock
    private PrintWriter mockPrintWriter;
    @Mock
    private Server mockServer;
    @Mock
    private Logger mockLogger;

    private ServerHandler receiverA;
    private ServerHandler receiverB;
    private ServerHandler senderC;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        receiverA = new ServerHandler(mockSocket, mockServer,
                mockBufferedReader, mockPrintWriter);
        receiverA.setClientName("A");

        receiverB = new ServerHandler(mockSocket, mockServer,
                mockBufferedReader, mockPrintWriter);
        receiverB.setClientName("B");

        senderC = new ServerHandler(mockSocket, mockServer,
                mockBufferedReader, mockPrintWriter);
        senderC.setClientName("C");
    }

    @After
    public void tearDown() throws Exception {
        mockSocket = null;
        mockBufferedReader = null;
        mockPrintWriter = null;
        receiverA = null;
        receiverB = null;
        senderC = null;
    }

    @Test
    public void broadcastMessage_success() {
        List serverHandlers = new ArrayList();

        when(mockServer.getServerHandles())
                .thenReturn(serverHandlers);

        serverHandlers.add(receiverA);
        serverHandlers.add(receiverB);
        serverHandlers.add(senderC);

        senderC.broadcastMessage("Broadcast");

        verify(mockPrintWriter, times(2)).println("Broadcast");
    }

    @Test
    public void whisperMessage_success() {
        List serverHandlers = new ArrayList();
        when(mockServer.getServerHandles())
                .thenReturn(serverHandlers);

        serverHandlers.add(receiverA);
        serverHandlers.add(receiverB);
        serverHandlers.add(senderC);

        senderC.whisperMessage("A","Whisper to A");
        senderC.whisperMessage("A","Whisper to A");
        senderC.whisperMessage("B","Whisper to B");

        verify(mockPrintWriter, times(2)).println("Whisper to A");
        verify(mockPrintWriter, times(1)).println("Whisper to B");
    }

    @Test
    public void createMessage_success() {
        ServerHandler handler = new ServerHandler(mockSocket, mockServer,
                mockBufferedReader, mockPrintWriter);

        handler.setClientName("A");
        String result = handler.createMessage("To be whispered", MessageType.WHISPER);
        String expected = "{\"messageType\":\"WHISPER\",\"sender\":\"SERVER\",\"receivers\":[\"A\"],\"content\":\"To be whispered\"}";
        Assert.assertEquals(expected, result);

        handler.setClientName("B");
        result = handler.createMessage("To be registered", MessageType.REGISTER);
        expected = "{\"messageType\":\"REGISTER\",\"sender\":\"SERVER\",\"receivers\":[\"B\"],\"content\":\"To be registered\"}";
        Assert.assertEquals(expected, result);

        handler.setClientName("C");
        result = handler.createMessage("To be broadcast", MessageType.GLOBAL);
        expected = "{\"messageType\":\"GLOBAL\",\"sender\":\"SERVER\",\"receivers\":[\"C\"],\"content\":\"To be broadcast\"}";
        Assert.assertEquals(expected, result);

        handler.setClientName("D");
        result = handler.createMessage("To be quited", MessageType.QUIT);
        expected = "{\"messageType\":\"QUIT\",\"sender\":\"SERVER\",\"receivers\":[\"D\"],\"content\":\"To be quited\"}";
        Assert.assertEquals(expected, result);
    }

    @Test
    public void handleMessage_whisper() {

        List serverHandlers = new ArrayList();
        when(mockServer.getServerHandles())
                .thenReturn(serverHandlers);

        when(mockServer.getLogger()).thenReturn(mockLogger);
        doNothing().when(mockLogger).info(anyString());

        serverHandlers.add(receiverA);
        serverHandlers.add(senderC);

        Message message = new Message();

        // Whisper
        message.setMessage("C", new String[]{"A"},
                MessageType.WHISPER, "whispers");

        senderC.handleMessage(message.getJsonString());
        String expected = "{\"messageType\":\"WHISPER\",\"sender\":\"C\",\"receivers\":[\"A\"],\"content\":\"C whispers: whispers\"}";
        verify(mockPrintWriter, times(1))
                .println(expected);
    }

    @Test
    public void handleMessage_broadcast() {

        List serverHandlers = new ArrayList();
        when(mockServer.getServerHandles())
                .thenReturn(serverHandlers);

        when(mockServer.getLogger()).thenReturn(mockLogger);
        doNothing().when(mockLogger).info(anyString());

        serverHandlers.add(receiverA);
        serverHandlers.add(senderC);

        Message message = new Message();

        // Broadcast
        message.setMessage("C", new String[]{},
                MessageType.GLOBAL, "broadcasts");

        senderC.handleMessage(message.getJsonString());
        String expected = "{\"messageType\":\"GLOBAL\",\"sender\":\"C\",\"receivers\":[],\"content\":\"C says: broadcasts\"}";
        verify(mockPrintWriter, times(1))
                .println(expected);
    }

    @Test
    public void handleMessage_quit() {

        List serverHandlers = new ArrayList();
        when(mockServer.getServerHandles())
                .thenReturn(serverHandlers);

        when(mockServer.getLogger()).thenReturn(mockLogger);
        doNothing().when(mockLogger).info(anyString());

        serverHandlers.add(receiverA);
        serverHandlers.add(senderC);

        Message message = new Message();

        // Quit
        message.setMessage("C", new String[]{},
                MessageType.QUIT, "");

        senderC.handleMessage(message.getJsonString());
        String expected = "{\"messageType\":\"GLOBAL\",\"sender\":\"C\",\"receivers\":[],\"content\":\"C leave the chat room\"}";
        verify(mockPrintWriter, times(1))
                .println(expected);
    }

    @Test
    public void handleMessage_registerOnce() {

        List serverHandlers = new ArrayList();
        when(mockServer.getServerHandles())
                .thenReturn(serverHandlers);

        when(mockServer.getLogger()).thenReturn(mockLogger);
        doNothing().when(mockLogger).info(anyString());

        serverHandlers.add(receiverA);

        Message message = new Message();

        // Register
        message.setMessage("SERVER", new String[]{},
                MessageType.REGISTER, "C");

        senderC.handleMessage(message.getJsonString());
        String expected = "{\"messageType\":\"SERVER\",\"sender\":\"SERVER\",\"receivers\":[],\"content\":\"REGISTER_OK\"}";
        verify(mockPrintWriter, times(1))
                .println(expected);
        expected = "{\"messageType\":\"GLOBAL\",\"sender\":\"SERVER\",\"receivers\":[],\"content\":\"C joins the chat room\"}";
        verify(mockPrintWriter, times(1))
                .println(expected);
    }

    @Test
    public void handleMessage_registerTriple() {

        List serverHandlers = new ArrayList();
        when(mockServer.getServerHandles())
                .thenReturn(serverHandlers);

        when(mockServer.getLogger()).thenReturn(mockLogger);
        doNothing().when(mockLogger).info(anyString());

        serverHandlers.add(receiverA);
        serverHandlers.add(receiverB);

        Message message = new Message();

        // Register first time: fail
        message.setMessage("SERVER", new String[]{},
                MessageType.REGISTER, "A");

        senderC.handleMessage(message.getJsonString());
        String expected = "{\"messageType\":\"SERVER\",\"sender\":\"SERVER\",\"receivers\":[],\"content\":\"REGISTER_FAILED\"}";
        verify(mockPrintWriter, times(1))
                .println(expected);

        // Register second time: fail
        message.setMessage("SERVER", new String[]{},
                MessageType.REGISTER, "B");

        senderC.handleMessage(message.getJsonString());
        expected = "{\"messageType\":\"SERVER\",\"sender\":\"SERVER\",\"receivers\":[],\"content\":\"REGISTER_FAILED\"}";
        verify(mockPrintWriter, times(2))
                .println(expected);

        // Register third time: success
        message.setMessage("SERVER", new String[]{},
                MessageType.REGISTER, "C");

        senderC.handleMessage(message.getJsonString());

        expected = "{\"messageType\":\"SERVER\",\"sender\":\"SERVER\",\"receivers\":[],\"content\":\"REGISTER_OK\"}";
        verify(mockPrintWriter, times(1))
                .println(expected);
        expected = "{\"messageType\":\"GLOBAL\",\"sender\":\"SERVER\",\"receivers\":[],\"content\":\"C joins the chat room\"}";
        verify(mockPrintWriter, times(2))
                .println(expected);
    }
}