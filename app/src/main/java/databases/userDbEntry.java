package databases;

//Class for DB Entry
public class userDbEntry {

    private long id;
    private long USER_ID;
    private String USER_NAME;
    private String USER_EMAIL;
    private String USER_PHONENUMBER;
    private String USER_PUBLICKEY;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(long id) {
        this.USER_ID = id;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String name) {
        this.USER_NAME = name;
    }

    public String getUSER_EMAIL() {
        return USER_EMAIL;
    }

    public void setUSER_EMAIL(String email) {
        this.USER_EMAIL = email;
    }

    public String getUSER_PHONENUMBER() {
        return USER_PHONENUMBER;
    }

    public void setUSER_PHONENUMBER(String phone) {
        this.USER_PHONENUMBER = phone;
    }

    public String getUSER_PUBLICKEY() {
        return USER_PUBLICKEY;
    }

    public void setUSER_PUBLICKEY(String publickey) {
        this.USER_PUBLICKEY = publickey;
    }
}
