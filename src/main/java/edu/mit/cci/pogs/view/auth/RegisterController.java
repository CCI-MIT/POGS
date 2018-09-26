package edu.mit.cci.pogs.view.auth;

import edu.mit.cci.pogs.service.UserService;
import edu.mit.cci.pogs.view.auth.beans.RegisterBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public RegisterBean registerBean() {
        return new RegisterBean();
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterBean registerBean) {

        userService.createUser(registerBean);
        return "redirect:/admin";
    }
}
