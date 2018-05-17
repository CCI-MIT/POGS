package edu.mit.cci.pogs.view.votingpooloption;

import edu.mit.cci.pogs.model.dao.votingpooloption.VotingPoolOptionDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolOption;
import edu.mit.cci.pogs.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/votingpooloption")
public class VotingPoolOptionController {

    @Autowired
    private VotingPoolOptionDao votingPoolOptionDao;

    @ModelAttribute("researchGroups")
    @GetMapping
    public String getAllVotingPoolOptions(Model model) {
        model.addAttribute("studiesList", votingPoolOptionDao.get());
        return "study/study-list";
    }

    @PostMapping
    public String saveVotingPoolOption(@ModelAttribute VotingPoolOption votingPoolOption, RedirectAttributes redirectAttributes) {
        if(votingPoolOption.getId() == null) {
            votingPoolOptionDao.create(votingPoolOption);
            MessageUtils.addSuccessMessage("Voting pool option created successfully!", redirectAttributes);
        } else {
            votingPoolOptionDao.update(votingPoolOption);
            MessageUtils.addSuccessMessage("Voting pool option updated successfully!", redirectAttributes);
        }

        return "redirect:/admin/votingpooloption";
    }


}
