package ServerHelper;


import Objects.Track;
import Private.ApiKeys;
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
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import Private.ApiKeys;

public class spotifyApiHelper {

    // private authorization key
    private String encoder;

    private ApiKeys Key;

//__________________________________________________________________________________________________________
//  בנאי של המחלקה
// __________________________________________________________________________________________________________
    public spotifyApiHelper() throws URISyntaxException, IOException, InterruptedException {

        // אובייקט ששומר את המפתחות + קישורים במיקום שבו הגיט לא עוקב
        Key = new ApiKeys();

        // מחרוזת אימות על מנת לקבל טוקן של המשתמש( יעבוד רק על המשתמש שלי - גל נגל)
        String authorizationCoder = Key.getUserId() + ':' + Key.getSecret();
        encoder = Base64.getEncoder().encodeToString(authorizationCoder.getBytes(StandardCharsets.UTF_8));

    }
// __________________________________________________________________________________________________________

    //  מטרת המתודה - קבלת טוקן
    // פרמטקין - קוד (מקבלים ספוטיפיי לאחר הפנייה לcallback), מחרוזת הפנייה
    public SpotifyApiToken getUserToken(String code, String redirect_uri) throws IOException, InterruptedException {

        // ניצור מחרוזת לקבלת טוקן
        String body = "code=" + code +
                    "&redirect_uri="+ redirect_uri +
                    "&grant_type=authorization_code";

        // נשלח בקשת http לספוטיפיי על מנת לקבל טוקן
        HttpRequest request = HttpRequest.newBuilder()
                .uri(Key.getGetTokenUri())
                .header("Authorization","Basic " + this.encoder)
                .headers("Content-Type","application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        // קבלת תשובה
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request,  HttpResponse.BodyHandlers.ofString());


        // נוציא את המידע וניצור אובייקט לשמירה
        return extract_token(response);
    }

    // נקרא את התשובה בנוגע לקבלת טוקן וניצור אובייקט לשמואר את המידע
    private SpotifyApiToken extract_token (HttpResponse<String> response) throws JsonProcessingException {

        // אובייקט לקריאת גייסון
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.readTree(response.body());

        // נוציא את המידע
        String token = rootNode.get("access_token").asText();
        String type = rootNode.get("token_type").asText();
        Long token_expire = rootNode.get("expires_in").asLong();

        //ניצור  תאריך תפוגה לטוקן
        LocalDateTime expires_in = LocalDateTime.now().plusSeconds(token_expire);

        // נחזיר אובייקט
        return new SpotifyApiToken(token,type,expires_in);
    }

    // מטרת המתודה - להחזיר רשימת של אובייקט track על מנת להציג למשתמש שירים לבחור לקבלת פלייליסט
    public ArrayList<Track> getTracks (SpotifyApiToken token) throws URISyntaxException, IOException, InterruptedException {

        // קישור לlast api על מנת לקבלת 50 שיריםן המושמעים ביותר (50 זה ברירת מחדל של הבקשה ניתן לבקש פחות)
        String url = "https://ws.audioscrobbler.com/2.0/?method=chart.gettoptracks" +
                "&api_key=" + Key.getLastApiKey() +
                "&format=json" +
                "&limit=1";

        // ניצור בקשה
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .build();

        // נשלח את הבקשה
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request,HttpResponse.BodyHandlers.ofString());

        // ניצור רשימה של אובייקטים
        return extract_Tracks(token,response);

    }

    // מטרת הפונקציה - ליצור רשימה של אובייקט שירים מתשובה של last api
    private ArrayList<Track> extract_Tracks(SpotifyApiToken token, HttpResponse<String>response) throws IOException, URISyntaxException, InterruptedException {

        // ניצור רשימה ריקה
        ArrayList<Track> tracks = new ArrayList<>();

        // ניצור אובייקט שיעזור לנו לעבור על תשובה
        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.readTree(response.body());

        JsonNode tracksData = jsonNode.path("tracks").path("track");

        // נעבור על כל השירים
        for (JsonNode track : tracksData){

            // נוציא את שם הפלייליסט ושם האומן
            String name = track.get("name").asText();
            String artistName = track.get("artist").get("name").asText();

            // נפנה ל spotify api על מנת לקבל עוד מידע על השיר
            tracks.add(GetSpotifyTrackData(token, name,artistName));

        }

        return tracks;
    }

    // מטרה לקבל עוד מידע על שיר מסוים על מנת לקבל אובייקט שיר
    private Track GetSpotifyTrackData(SpotifyApiToken token, String name, String artistName) throws URISyntaxException, IOException, InterruptedException {

        // נבקש מספוטיפי לקבל מידע על שיר מסויים של אומן מסויים
        String url = Key.getSearchUrl()+
                URLEncoder.encode("track:" + name + " artist:" + artistName,StandardCharsets.UTF_8 )+
                "&type=track"+
                "&limit=1";

        // נשלח בקשה
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .headers("Authorization",token.getType() + " " + token.getToken())
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request,HttpResponse.BodyHandlers.ofString());


        return extractTrackData(response ,name, artistName);

    }

    //
    private Track extractTrackData(HttpResponse<String> response, String name, String artistName) throws JsonProcessingException {

        String id = "";
        String imgUrl = "";

        ObjectMapper mapper = new ObjectMapper();

        JsonNode jsonNode = mapper.readTree(response.body());

        System.out.println(jsonNode.path("name"));

        System.out.println(
                mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(jsonNode)
        );

       return new Track(id,name,artistName,imgUrl);
    }

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

//__________________________________________________________________________________________________________
//  Getters
// __________________________________________________________________________________________________________

    public String geAuthorizeUri(){
        return Key.getAuthorizeUri();
    }

    public String getUserId(){
        return Key.getUserId();
    }

    public String getScope(){
        return Key.getScope();
    }


};
