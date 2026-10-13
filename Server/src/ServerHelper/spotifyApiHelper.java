package ServerHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

public class spotifyApiHelper {

    // user id
    final private String UserId = "bce0c09c5e754f7f9d528abcfbcf5fe2";
    // user secret
    final private String secret = "46b878da0a5243c6a667797f8e19dde7";

    // url for authorize user
    private String authorizeUri = "https://accounts.spotify.com/authorize?";

    // spotify authorization url
    private URI GetTokenUrl = new URI("https://accounts.spotify.com/api/token");


    // private authorization key
    private String encoder;

    private Token token;

    public spotifyApiHelper() throws URISyntaxException {
        String authorizationCoder = UserId + ':' + secret;
        encoder = Base64.getEncoder().encodeToString(authorizationCoder.getBytes(StandardCharsets.UTF_8));
    }


    // getters
    public String geAuthorizeUri(){return authorizeUri;}

    public String getUserId(){return UserId;}

    // get token for a Specific User
    public Token getUserToken(String code,String redirect_uri) throws IOException, InterruptedException {
        String body = "code=" + code +
                    "&redirect_uri="+ redirect_uri +
                    "&grant_type=client_credentials";

        // sending http request in order to get a token
        HttpRequest request = HttpRequest.newBuilder()
                .uri(GetTokenUrl)
                .header("Authorization","Basic " + this.encoder)
                .headers("Content-Type"," application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        // get a response
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request,  HttpResponse.BodyHandlers.ofString());

        // extract data and return token type data
        return extract_token(response);
    }

    // read json response from spotify api in order to get token
    private Token extract_token (HttpResponse<String> response) throws JsonProcessingException {

        // will be used in order to read json
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(response.body());

        // extract data from json
        String token = rootNode.get("access_token").asText();
        String type = rootNode.get("token_type").asText();
        Long token_expire = rootNode.get("expires_in").asLong();

        // create expire time
        LocalDateTime expires_in = LocalDateTime.now().plusSeconds(token_expire);

        return new Token(token,type,expires_in);
    }

    // create playlist with a giving name
    private void create_playlist(String name){

    }

}
