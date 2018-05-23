package edu.mit.cci.pogs.view.votingpoolvote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.mit.cci.pogs.model.dao.votingpoolvote.VotingPoolVoteDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolVote;
import edu.mit.cci.pogs.utils.MessageUtils;

@Controller
@RequestMapping(value = "/admin/votingpoolvote")
public class VotingPoolVoteController {

    @Autowired
    private VotingPoolVoteDao votingPoolVoteDao;

    @ModelAttribute("researchGroups")
    @GetMapping
    public String getAllVotingPoolVote(Model model) {
        model.addAttribute("studiesList", votingPoolVoteDao.get());
        return "study/study-list";
    }

    @PostMapping
    public String saveVotingPoolVote(@ModelAttribute VotingPoolVote votingPoolVote, RedirectAttributes redirectAttributes) {

        if (votingPoolVote.getId() == null) {
            votingPoolVoteDao.create(votingPoolVote);
            MessageUtils.addSuccessMessage("Voting pool vote created successfully!", redirectAttributes);
        } else {
            votingPoolVoteDao.update(votingPoolVote);
            MessageUtils.addSuccessMessage("Voting pool vote updated successfully!", redirectAttributes);
        }
        return "redirect:/admin/votingpoolvote";
    }
}
