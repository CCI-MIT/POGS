package edu.mit.cci.pogs.view.auth;

import edu.mit.cci.pogs.service.UserService;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.view.auth.beans.RegisterBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

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
    public String register(@ModelAttribute @Valid RegisterBean registerBean, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        if (userService.createUser(registerBean) == null) {
            MessageUtils.addErrorMessage("User failed to create", redirectAttributes);
            return "redirect:/register";
        }

        MessageUtils.addSuccessMessage("User created successfully!", redirectAttributes);
        return "redirect:/admin";
    }
}
