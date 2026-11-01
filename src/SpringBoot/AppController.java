package SpringBoot;

import ServerHelper.LastToken;
import ServerHelper.SpotifyApiToken;
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
import java.security.NoSuchAlgorithmException;

import ServerHelper.spotifyApiHelper;


@Controller
public class AppController {

    // app controller helper
    private spotifyApiHelper helper;
    private SpotifyApiToken spotifyApiToken;
    private LastToken lastToken;

    private String redirect_uri = URLEncoder.encode("http://127.0.0.1:8080/spotify/callback", StandardCharsets.UTF_8);


    public AppController() throws URISyntaxException, IOException, InterruptedException {
        this.helper = new spotifyApiHelper();
    }

    //                                      עמוד ראשוני
    //__________________________________________________________________________________
    // נפנה את המשתמש לעמוד האינטרנטי של spotify במטרה לקבל קוד הישמש אותנו לקבלת טוקן
    @GetMapping("/")
    public RedirectView getSpotifyApiToken() {

        //  נרכיב  קישור בו יש:
        //   מזהה לקוח (עבור האפליקציה שלנו זה מפתח אישי של המשתמש שלי)
        // כתובת חזרה שאנחנו רוצים להפנות את המשתמש לאחר אישור שימוש במידע פרטי
        // לאיזה מידע אנחנו רוצים לגשת
        String Url = helper.geAuthorizeUri() +
                "response_type=code" +
                "&client_id=" + helper.getUserId() +
                "&redirect_uri=" + redirect_uri +
                "&scope=" + helper.getScope() +
                "&show_dialog=true";

        // נשלח את המשתמש לקישור
        return new RedirectView(Url);
    }

    // לאחר אישור ספוטיפי יפנה אותנו לעמוד שהגדרנו לו כעמוד חזרה
    // נמשוך מהקישור את הקוד שקיבלנו וניצור טוקן של המשתמש שלנו
    @GetMapping("/spotify/callback")
    public RedirectView logInPage(@RequestParam(required = false) String code,
                                  @RequestParam(required = false) String error,
                                  Model model) throws URISyntaxException, IOException, InterruptedException {

        // אם יש שגיאה
        if (error != null) {
            System.out.println("error");
            return new RedirectView("/");
        }

        // יצוא טוקן על מנת לתקשר עם spotify
        // נשמור אותו כאובייקט פרטי של האפליקציה
        this.spotifyApiToken = helper.getUserToken(code, redirect_uri);

        //  נפנה את המשתמש לעמוד הבית
        return new RedirectView("/HomePage");
    }

    // עמוד הבית
    //___________________________________________________________________________
    @GetMapping("/HomePage")
    public String HomePage(@RequestParam(required = false) String error,Model model)
            throws URISyntaxException, IOException, InterruptedException, NoSuchAlgorithmException {

        // אם יש שגיאה
        if (error != null) {
            System.out.println("error");
            return "";
        }

        // נמשוך רשימה של שירים כדי שהמשתמש יבחר איזה שירים הוא מעדיף שיהיו בסיס הפלייליסט
        model.addAttribute("tracks",helper.getTracks(this.spotifyApiToken));

        // נבקש את העמוד של יצירת הפלייליסט
        return "CreatePlaylist.html";
    }

    // במקרה של בקשת יצירת פלייליסט
    //_____________________________________________________________
    // נבקש פרמטרים: שם פלייליסט, תיאור הפלייליסט
    @PostMapping("/CreatePlaylist")
    public String createPlaylist(@RequestParam(required = false) String playlistName,
                                 @RequestParam(required = false) String playlistDescription,
                                 @RequestParam(required = false) String error) throws URISyntaxException, IOException, InterruptedException {

        // אם יש שגיאה
        if (error != null) {
            System.out.println("error");
            return " ";
        }

        // ניצור פלייליסט ריק
//            helper.create_playlist(token,playlistName,playlistDescription);

            String artistSeed = "1IAEef07H0fd9aA8aUHUlL";
            String genreSeed = "";
            String trackSeed = "";
            double target_energy = 0.5;
            int limit = 10;

        // ניצור את תוכן הפלייליסט לפי last api והשירים שהמשתמש בחר
//            helper.getRecommendations(token,artistSeed,genreSeed,trackSeed,target_energy,limit);

        // נעדכן את הפלייליסט
//            helper.getGenres(token);
            helper.create_playlist(spotifyApiToken,playlistName,playlistDescription);
            return "CreatePlaylist.html";
        }
}


