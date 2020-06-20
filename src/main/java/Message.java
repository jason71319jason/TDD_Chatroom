import org.json.JSONArray;
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

    public void setMessage(String sender, String[] receivers,
                           MessageType type, String content) {
        this.sender = sender;
        this.receivers = receivers;
        this.messageType = type;
        this.content = content;
    }

    public void setMessageByJson(JSONObject jsonObject) {
        // sender
        this.sender = jsonObject.getString("sender");
        // receivers
        JSONArray jsonArray = jsonObject.getJSONArray("receivers");
        receivers = new String[jsonArray.length()];
        for (int i=0; i<jsonArray.length(); i++) {
            receivers[i] = jsonArray.getString(i);
        }
        // messageType
        this.messageType = MessageType.valueOf(jsonObject.getString("messageType"));
        // content
        this.content = jsonObject.getString("content");
    }

    public String getContent() { return this.content; }
    public String[] getReceivers() { return this.receivers; }
    public MessageType getMessageType() { return this.messageType; }
    public String getSender() { return  this.sender; }
}
