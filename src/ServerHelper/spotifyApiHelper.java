package ServerHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.message.Message;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class spotifyApiHelper {

    // user id
    final private String UserId = "bce0c09c5e754f7f9d528abcfbcf5fe2";
    // user secret
    final private String secret = "46b878da0a5243c6a667797f8e19dde7";

    // url for authorize user
    private String authorizeUri = "https://accounts.spotify.com/authorize?";

    // spotify authorization uri
    private URI GetTokenUri = new URI("https://accounts.spotify.com/api/token");

    private String scope = URLEncoder.encode("playlist-modify-private playlist-read-private",StandardCharsets.UTF_8);

    private URI GetUserPlaylistsUri = new URI("https://api.spotify.com/v1/me/playlists");

    private String getRecommendationsUri = "https://api.spotify.com/v1/recommendations?";

    public String LastApiKey = "a8524c28758188a8ef97f9a757b14d22";

    public String LastSecrete = "750a5a974ea0e3a986097dbecf079d64";

    // private authorization key
    private String encoder;


    public spotifyApiHelper() throws URISyntaxException {
        String authorizationCoder = UserId + ':' + secret;
        encoder = Base64.getEncoder().encodeToString(authorizationCoder.getBytes(StandardCharsets.UTF_8));
    }


    // getters
    public String geAuthorizeUri(){return authorizeUri;}

    public String getUserId(){return UserId;}

    public String getScope(){return scope;}

    // get token for a Specific User
    public SpotifyApiToken getUserToken(String code, String redirect_uri) throws IOException, InterruptedException {

        String body = "code=" + code +
                    "&redirect_uri="+ redirect_uri +
                    "&grant_type=authorization_code";

        // sending http request in order to get a token
        HttpRequest request = HttpRequest.newBuilder()
                .uri(GetTokenUri)
                .header("Authorization","Basic " + this.encoder)
                .headers("Content-Type","application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        // get a response
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request,  HttpResponse.BodyHandlers.ofString());


        // extract data and return token type data
        return extract_token(response);
    }

    // read json response from spotify api in order to get token
    private SpotifyApiToken extract_token (HttpResponse<String> response) throws JsonProcessingException {

        // will be used in order to read json
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(response.body());

        // extract data from json
        String token = rootNode.get("access_token").asText();
        String type = rootNode.get("token_type").asText();
        Long token_expire = rootNode.get("expires_in").asLong();

        // create expire time
        LocalDateTime expires_in = LocalDateTime.now().plusSeconds(token_expire);

        return new SpotifyApiToken(token,type,expires_in);
    }

//    public boolean playlistExists(Token token,String name) throws IOException, InterruptedException {
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(GetUserPlaylistsUrl)
//                .headers("Authorization",token.getType() + " " + token.getToken())
//                .headers("Content-Type","application/json")
//                .GET()
//                .build();
//
//        HttpResponse <String> response =  HttpClient.newHttpClient()
//                .send(request,  HttpResponse.BodyHandlers.ofString());
//
//        System.out.println("playlistExists methode response " +  response.body());
//
//        return true;
//    }

    // create playlist with a giving name
    public void create_playlist(SpotifyApiToken token, String name, String description) throws URISyntaxException, IOException, InterruptedException {

        Map<String,Object> jsonData = new HashMap<>();

        jsonData.put("name",name);
        jsonData.put("description",description);
        jsonData.put("public",false);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(jsonData);

        HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.spotify.com/v1/me/playlists"))
                    .header("Authorization",token.getType() + " " + token.getToken())
                    .headers("Content-Type","application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();



        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request,  HttpResponse.BodyHandlers.ofString());

        System.out.println("create_playlist methode response: " + response);
        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());


    }


}
