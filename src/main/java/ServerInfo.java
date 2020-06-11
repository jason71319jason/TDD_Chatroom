public class ServerInfo {
    private String hostname;
    private int port;
    public void setHostname (String host) {
        hostname = host;
    }
    public void setPort (int port_num) {
        port = port_num;
    }
    public String getHostname () {
        return hostname;
    }
    public int getPort () {
        return port;
    }
}
