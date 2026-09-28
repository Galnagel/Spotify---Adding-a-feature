import java.sql.Time;

public class Token {

    private String token;
    private String type;


    public Token(String token, String type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public Token setToken(String token) {
        this.token = token;
        return this;
    }

    public String getType() {
        return type;
    }

    public Token setType(String type) {
        this.type = type;
        return this;
    }
}
