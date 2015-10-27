package items;


public class contactItem {

    private long id;
    private String name;
    private String email;

    public contactItem(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getID(){
        return id;
    }

    public void setID(Long id){
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getemail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
