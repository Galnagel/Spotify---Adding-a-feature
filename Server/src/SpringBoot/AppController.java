package SpringBoot;

import ServerHelper.Token;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import ServerHelper.spotifyApiHelper;



@Controller
public class AppController {

    // app controller helper
    private spotifyApiHelper helper;
    private Token token;

    private String redirect_uri = URLEncoder.encode("http://127.0.0.1:8080/spotify/callback", StandardCharsets.UTF_8);


    public AppController() throws URISyntaxException, IOException, InterruptedException {
        this.helper = new spotifyApiHelper();
    }


    @GetMapping("/")
    public RedirectView getToken(){
        String Url = helper.geAuthorizeUri()+
                "response_type=code" +
                "&client_id=" + helper.getUserId() +
                "&redirect_uri=" + redirect_uri +
                "&scope=" + helper.getScope()+
                "&show_dialog=true";

         return new RedirectView(Url);
    }

    @GetMapping("/spotify/callback")
    public String logInPage(@RequestParam(required = false)String code,
                            @RequestParam(required = false)String error,
                            Model model) throws URISyntaxException, IOException, InterruptedException {

        if(error != null) {
            System.out.println("error");
            return "";
        }

       this.token = helper.getUserToken(code,redirect_uri);

        return "CreatePlaylist.html";
    }

    @PostMapping("spotify/CreatePlaylist")
    public String createPlaylist(@RequestParam(required = false)String playlistName,
                               @RequestParam(required = false)String playlistDescription,
                               @RequestParam(required = false)String error) throws URISyntaxException, IOException, InterruptedException {

        if(error != null) {
            System.out.println("error");
            return " ";
        }

        else {
            helper.create_playlist(token,playlistName,playlistDescription);

//            helper.create_playlist(token,playlistName,playlistDescription);
            return "CreatePlaylist.html";
        }
    }

}
