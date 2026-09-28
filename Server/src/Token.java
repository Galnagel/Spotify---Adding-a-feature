import java.sql.Date;
import java.sql.Time;

public class Token {

    private String token;
    private String type;
    private Date expires_in;

    public Token(String token, String type, Date expires_in) {
        this.token = token;
        this.type = type;
        this.expires_in = expires_in;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Date getExpires_in() {
        return expires_in;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExpires_in(Date expires_in) {
        this.expires_in = expires_in;
    }
}
