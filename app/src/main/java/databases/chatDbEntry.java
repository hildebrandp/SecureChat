package databases;


public class chatDbEntry {
    private long id;
    private long CHAT_ID;
    private long CHAT_SENDER_ID;
    private long CHAT_RECIEVER_ID;
    private String CHAT_MESSAGE;
    private String CHAT_READ;
    private String CHAT_DATE;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCHAT_ID() {
        return CHAT_ID;
    }

    public void setCHAT_ID(long id) {
        this.CHAT_ID = id;
    }

    public long getCHAT_SENDER_ID() {
        return CHAT_SENDER_ID;
    }

    public void setCHAT_SENDER_ID(long CHAT_SENDER_ID) {
        this.CHAT_SENDER_ID = CHAT_SENDER_ID;
    }

    public long getCHAT_RECIEVER_ID() {
        return CHAT_RECIEVER_ID;
    }

    public void setCHAT_RECIEVER_ID(long CHAT_RECIEVER_ID) {
        this.CHAT_RECIEVER_ID = CHAT_RECIEVER_ID;
    }

    public String getCHAT_MESSAGE() {
        return CHAT_MESSAGE;
    }

    public void setCHAT_MESSAGE(String CHAT_MESSAGE) {
        this.CHAT_MESSAGE = CHAT_MESSAGE;
    }

    public String getCHAT_READ() {
        return CHAT_READ;
    }

    public void setCHAT_READ(String CHAT_READ) {
        this.CHAT_READ = CHAT_READ;
    }

    public String getCHAT_DATE() {
        return CHAT_DATE;
    }

    public void setCHAT_DATE(String CHAT_DATE) {
        this.CHAT_DATE = CHAT_DATE;
    }
}
