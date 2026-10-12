package SpringBoot;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class Controller {

    @GetMapping("/")
    public String LoginPage(Model model){
        model.addAttribute("");
         return "logIn";
    }
}
