package edu.mit.cci.pogs.view.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import edu.mit.cci.pogs.config.AuthUserDetailsService;
import edu.mit.cci.pogs.service.UserService;

@Controller
public class InitialSetup {

    @Autowired
    private UserService userService;


    @GetMapping("/initialize")
    public String showIndex(Model model) {

        if(userService.hasAuthUsers()){
            model.addAttribute("alreadyInitialized", true);
        } else {
            model.addAttribute("alreadyInitialized", false);
        }

        return "initialize";
    }
}
