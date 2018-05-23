package edu.mit.cci.pogs.view.authuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import javax.validation.Valid;

import edu.mit.cci.pogs.model.dao.researchgroup.ResearchGroupDao;
import edu.mit.cci.pogs.model.dao.user.UserDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.AuthUser;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroup;
import edu.mit.cci.pogs.service.UserService;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;

@Controller
@RequestMapping(value = "/admin/users")
public class AuthUserController {

    @Autowired
    private UserDao authUserDao;

    @Autowired
    private ResearchGroupDao researchGroupDao;

    @Autowired
    private UserService userService;

    @ModelAttribute("researchGroups")
    public List<ResearchGroup> getAllResearchGroups() {

        return researchGroupDao.list();
    }

    @GetMapping
    public String getAuthUser(Model model) {

        model.addAttribute("usersList", authUserDao.list());
        return "user/user-list";
    }

    @GetMapping("{id}")
    public String getAuthUsers(@PathVariable("id") Long id, Model model) {

        model.addAttribute("authUser", authUserDao.get(id));
        return "user/user-display";
    }

    @GetMapping("/create")
    public String createAuthUser(Model model) {

        model.addAttribute("authUser", new AuthUserBean(new AuthUser()));
        return "user/user-edit";
    }

    @GetMapping("{id}/edit")
    public String createAuthUser(@PathVariable("id") Long id, Model model) {
        AuthUserBean aub = new AuthUserBean(authUserDao.get(id));
        aub.setResearchGroupRelationshipBean(
                new ResearchGroupRelationshipBean());
        aub.getResearchGroupRelationshipBean()
                .setResearchGroupHasAuthUsersSelectedValues(
                        userService.listResearchGroupHasAuthUserByAuthUser(id));

        model.addAttribute("authUser", aub);
        return "user/user-edit";
    }

    @PostMapping
    public String saveAuthUser(@Valid @ModelAttribute AuthUserBean authUserBean, RedirectAttributes redirectAttributes) {

        userService.adminCreateOrUpdateUser(authUserBean);

        if (authUserBean.getId() == null) {
            MessageUtils.addSuccessMessage("AuthUser created successfully!", redirectAttributes);
        } else {
            MessageUtils.addSuccessMessage("AuthUser updated successfully!", redirectAttributes);
        }

        return "redirect:/admin/users";
    }

}
