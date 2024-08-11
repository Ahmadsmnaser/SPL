package bgu.spl.net.impl.tftp;

public class ClientStatus {
    private boolean shouldTerminate;
    private boolean WRQrequsted;
    private boolean WRQstarted;
    private boolean WRQfinished;
    private boolean LogedIn;
    private boolean logingreq;
    private String lastCommand;
    private boolean RRQrequsted;
    private boolean RRQstarted;
    private boolean RRQfinished;
    private boolean DIRQrequsted;
    private boolean DIRQstarted;
    private boolean DIRQfinished;
    private String FileName;
    private byte[] data;
    private int index;

    public ClientStatus() {
        this.shouldTerminate = false;
        this.WRQrequsted = false;
        this.WRQstarted = false;
        this.WRQfinished = false;
        this.LogedIn = false;
        this.logingreq = false;
        this.lastCommand = "";
        this.RRQrequsted = false;
        this.RRQstarted = false;
        this.RRQfinished = false;
        this.DIRQrequsted = false;
        this.DIRQstarted = false;
        this.DIRQfinished = false;
        this.FileName = "";
        this.data = new byte[512];
        this.index = 0;

    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public void setShouldTerminate(boolean shouldTerminate) {
        this.shouldTerminate = shouldTerminate;
    }

    public boolean isWRQrequsted() {
        return WRQrequsted;
    }

    public void setWRQrequsted(boolean WRQrequsted) {
        this.WRQrequsted = WRQrequsted;
    }

    public boolean isWRQstarted() {
        return WRQstarted;
    }

    public void setWRQstarted(boolean WRQstarted) {
        this.WRQstarted = WRQstarted;
    }

    public boolean isWRQfinished() {
        return WRQfinished;
    }

    public void setWRQfinished(boolean WRQfinished) {
        this.WRQfinished = WRQfinished;
    }

    public boolean isLogedIn() {
        return LogedIn;
    }

    public void setLogedIn(boolean LogedIn) {
        this.LogedIn = LogedIn;
    }

    public boolean isLogingreq() {
        return logingreq;
    }

    public void setLogingreq(boolean logingreq) {
        this.logingreq = logingreq;
    }

    public String getLastCommand() {
        return lastCommand;
    }

    public void setLastCommand(String lastCommand) {
        this.lastCommand = lastCommand;
    }

    public boolean isRRQrequsted() {
        return RRQrequsted;
    }

    public void setRRQrequsted(boolean RRQrequsted) {
        this.RRQrequsted = RRQrequsted;
    }

    public boolean isRRQstarted() {
        return RRQstarted;
    }

    public void setRRQstarted(boolean RRQstarted) {
        this.RRQstarted = RRQstarted;
    }

    public boolean isRRQfinished() {
        return RRQfinished;
    }

    public void setRRQfinished(boolean RRQfinished) {
        this.RRQfinished = RRQfinished;
    }

    public boolean isDIRQrequsted() {
        return DIRQrequsted;
    }

    public void setDIRQrequsted(boolean DIRQrequsted) {
        this.DIRQrequsted = DIRQrequsted;
    }

    public boolean isDIRQstarted() {
        return DIRQstarted;
    }

    public void setDIRQstarted(boolean DIRQstarted) {
        this.DIRQstarted = DIRQstarted;
    }

    public boolean isDIRQfinished() {
        return DIRQfinished;
    }

    public void setDIRQfinished(boolean DIRQfinished) {
        this.DIRQfinished = DIRQfinished;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
