package edu.mit.cci.pogs.view.votingpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.mit.cci.pogs.model.dao.votingpool.VotingPoolDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.VotingPool;
import edu.mit.cci.pogs.utils.MessageUtils;

@Controller
@RequestMapping(value = "/admin/votingpool")
public class VotingPoolController {
    @Autowired
    private VotingPoolDao votingPoolDao;

    @ModelAttribute("researchGroups")
    public String getAllVotingPools(Model model) {

        model.addAttribute("studiesList", votingPoolDao.get());
        return "study/study-list";
    }

    @PostMapping
    public String saveVotingPool(@ModelAttribute VotingPool votingPool, RedirectAttributes redirectAttributes) {

        if (votingPool.getId() == null) {
            votingPoolDao.create(votingPool);
            MessageUtils.addSuccessMessage("Voting pool created successfully!", redirectAttributes);
        } else {
            votingPoolDao.update(votingPool);
            MessageUtils.addSuccessMessage("Voting pool updated successfully!", redirectAttributes);
        }

        return "redirect:/admin/votingpool";
    }
}
