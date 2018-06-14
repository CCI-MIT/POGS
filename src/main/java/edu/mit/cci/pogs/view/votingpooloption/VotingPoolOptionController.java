package edu.mit.cci.pogs.view.votingpooloption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.mit.cci.pogs.model.dao.votingpooloption.VotingPoolOptionDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolOption;
import edu.mit.cci.pogs.utils.MessageUtils;

@Controller
@RequestMapping(value = "/admin/votingpooloptions")
public class VotingPoolOptionController {

    @Autowired
    private VotingPoolOptionDao votingPoolOptionDao;

    @GetMapping
    public String getVotingPoolOption(Model model) {
        model.addAttribute("votingpooloptionList", votingPoolOptionDao.list());
        return "votingpooloption/votingpooloption-list";
    }

    @GetMapping("/create")
    public String createVotingPoolOption(Model model) {

        VotingPoolOption votingPoolOption = new VotingPoolOption();
        model.addAttribute("votingpooloption", votingPoolOption);
        return "votingpooloption/votingpooloption-edit";
    }

    @GetMapping("{votingPoolOptionId}/edit")
    public String editVotingPoolOption(@PathVariable("votingPoolOptionId") Long votingPoolOptionId, Model model) {

        VotingPoolOption votingPoolOption = new VotingPoolOption(votingPoolOptionDao.get(votingPoolOptionId));
        model.addAttribute("votingpooloption", votingPoolOption);
        return "votingpooloption/votingpooloption-edit";
    }
    @PostMapping
    public String saveVotingPoolOption(@ModelAttribute VotingPoolOption votingPoolOption, RedirectAttributes redirectAttributes) {
        if (votingPoolOption.getId() == null) {
            votingPoolOptionDao.create(votingPoolOption);
            MessageUtils.addSuccessMessage("Voting pool option created successfully!", redirectAttributes);
        } else {
            votingPoolOptionDao.update(votingPoolOption);
            MessageUtils.addSuccessMessage("Voting pool option updated successfully!", redirectAttributes);
        }

        return "redirect:/admin/votingpooloption";
    }


}
