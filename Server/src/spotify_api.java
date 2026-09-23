import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Flow;

public class spotify_api {

    // my id + secret
    final private String id = "bce0c09c5e754f7f9d528abcfbcf5fe2";
    final private String secret = "f122d10a2f424b2186804f0458cfd42b";

    // spotify authorization url
    private URI url = new URI("https://accounts.spotify.com/api/token");

    // private authorization key
    private String encoder;

    private String token;

    public spotify_api() throws URISyntaxException, IOException, InterruptedException {

        // authorization key encode
        String authorization_String = id + ":" + secret;

        // encode in utf_8 format
        byte [] authorization_String_utf8 = authorization_String.getBytes(StandardCharsets.UTF_8);
        this.encoder = Base64.getEncoder().encodeToString(authorization_String_utf8);

        Get_Authorization_token();
    }


    private void Get_Authorization_token () throws IOException, InterruptedException {

        // send id + secrete in oder to get a token using http post request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(this.url)
                .header("Authorization","Basic " + this.encoder)
                .headers("Content-Type"," application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();


        HttpResponse<String> response = HttpClient.newHttpClient()
                                        .send(request,  HttpResponse.BodyHandlers.ofString());

        this.token = extract_token(response);

    }

    private String extract_token (HttpResponse<String> response){

        System.out.println(response.body());


        return  " ";
    }

    // create playlist with a giving name
    private void create_playlist(String name){

    }
}
