package edu.mit.cci.pogs.view.votingpoolvote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.mit.cci.pogs.model.dao.votingpoolvote.VotingPoolVoteDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPoolVote;
import edu.mit.cci.pogs.utils.MessageUtils;

@Controller
@RequestMapping(value = "/admin/votingpoolvotes")
public class VotingPoolVoteController {

    @Autowired
    private VotingPoolVoteDao votingPoolVoteDao;

    @GetMapping
    public String getVotingPoolVote(Model model) {
        model.addAttribute("votingpoolvoteList", votingPoolVoteDao.list());
        return "votingpoolvote/votingpoolvote-list";
    }

    @GetMapping("/create")
    public String createVotingPoolVote(Model model) {

        VotingPoolVote votingPoolVote = new VotingPoolVote();
        model.addAttribute("votingpoolvote", votingPoolVote);
        return "votingpoolvote/votingpoolvote-edit";
    }

    @GetMapping("{votingPoolVoteId}/edit")
    public String editVotingPoolOption(@PathVariable("votingPoolVoteId") Long votingPoolVoteId, Model model) {

        VotingPoolVote votingPoolVote = new VotingPoolVote(votingPoolVoteDao.get(votingPoolVoteId));
        model.addAttribute("votingpoolvote", votingPoolVote);
        return "votingpoolvote/votingpoolvote-edit";
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
