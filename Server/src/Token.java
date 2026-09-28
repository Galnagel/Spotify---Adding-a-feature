import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

public class Token {

    private String token;
    private String type;
    private LocalDateTime expires_in;

    public Token(String token, String type, LocalDateTime expires_in) {
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


    public void setToken(String token) {
        this.token = token;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(LocalDateTime expires_in) {
        this.expires_in = expires_in;
    }
}
