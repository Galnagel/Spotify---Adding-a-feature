import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class spotify_api {

    final private String id = "bce0c09c5e754f7f9d528abcfbcf5fe2";
    final private String secret = "f122d10a2f424b2186804f0458cfd42b";

    private String getToken(){

        String auth_String = id + ":" + secret;

        byte [] auth_String_utf8 = auth_String.getBytes(StandardCharsets.UTF_8);
        String encoder = Base64.getEncoder().encodeToString(auth_String_utf8);

        String url = "https://accounts.spotify.com/api/token";

        return encoder;
    }

    // create playlist with a giving name
    private void create_playlist(String name){

    }
}
