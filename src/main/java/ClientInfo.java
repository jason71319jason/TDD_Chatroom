import org.json.JSONObject;

public class ClientInfo {
    private String username = null;
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
        if (username.equals(null))
            throw new NullPointerException("Client name is not define.");
        return this.username;
    }

}
