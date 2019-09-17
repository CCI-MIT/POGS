package edu.mit.cci.pogs.view.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(HttpServletRequest request, Model model, @RequestParam(value = "error", required = false) String error) {

        if(error  != null)
        {
            model.addAttribute("status", "Invalid credentials. Please try again.");
        }
        else
        {
            model.addAttribute("status", "");
        }

        return "auth/login";
    }
}
