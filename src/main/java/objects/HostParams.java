package objects;

/**
 * Created by VSKryukov on 28.01.2016.
 */
public class HostParams {
    private String host;
    private String user;
    private String pass;
    private int port;
    private String sourceDir;
    private String targetDir;
    private int timeOut;
    private String protocol;

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public int getPort() {
        return port;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public String getProtocol() {
        return protocol;
    }

    public HostParams(String[] params) {
        this.host = params[0];
        this.user = params[1];
        this.pass = params[2];
        this.port = Integer.parseInt(params[3]);
        this.targetDir = params[4];
        this.sourceDir = "";
        this.timeOut = Integer.parseInt(params[5]);
        this.protocol = params[6];
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Host:").append(host).append("; ")
                .append("USER:").append(user).append("; ")
                .append("PASS:").append(pass).append("; ")
                .append("PORT:").append(port).append("; ")
                .append("Target Dir:").append(targetDir).append("; ")
                .append("Source Dir:").append((sourceDir == "" ? "Not set" : sourceDir)).append("; ")
                .append("Protocol:").append(protocol).append("; ")
                .append("TimeOut:").append(timeOut)
                .toString();
    }

    public void print() {
        System.out.println(this.toString());
    }
}
