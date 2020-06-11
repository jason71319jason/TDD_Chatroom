import org.json.JSONObject;

public class ClientInfo {
    private String username = "";
    /**
     *
     * @param username client's name
     */
//    public ClientInfo(String username) {
//        this.username = username;
//    }

    /**
     *
     * @return
     */
    public void setName(String name) {username = name;}
    public String getName() {
        return this.username;
    }

}
