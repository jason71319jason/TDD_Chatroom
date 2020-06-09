import org.json.JSONObject;

public class Message {
    public String sender;
    public String[] receivers;
    public MessageType messageType;
    public String content;

    public String getJsonString() {
        JSONObject object = new JSONObject();
        object.put("sender", sender);
        object.put("receivers", receivers);
        object.put("messageType", messageType);
        object.put("content", content);
        return object.toString();
    }

}
