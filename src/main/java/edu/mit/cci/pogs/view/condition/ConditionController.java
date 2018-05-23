package edu.mit.cci.pogs.view.condition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.mit.cci.pogs.model.dao.condition.ConditionDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Condition;
import edu.mit.cci.pogs.utils.MessageUtils;

@Controller
@RequestMapping(value = "/admin/conditions")
public class ConditionController {

    @Autowired
    private ConditionDao conditionDao;

    @GetMapping
    public String getCondition(Model model) {

        model.addAttribute("conditionsList", conditionDao.list());
        return "condition/condition-list";
    }

    @GetMapping("{id}")
    public String getConditions(@PathVariable("id") Long id, Model model) {

        model.addAttribute("condition", conditionDao.get(id));
        return "condition/condition-display";
    }

    @GetMapping("/create")
    public String createCondition(Model model) {

        model.addAttribute("condition", new Condition());
        return "condition/condition-edit";
    }

    @GetMapping("{id}/edit")
    public String createCondition(@PathVariable("id") Long id, Model model) {
        model.addAttribute("condition", conditionDao.get(id));
        return "condition/condition-edit";
    }

    @PostMapping
    public String saveCondition(@ModelAttribute Condition condition, RedirectAttributes redirectAttributes) {

        if (condition.getId() == null) {
            conditionDao.create(condition);
            MessageUtils.addSuccessMessage("Condition created successfully!", redirectAttributes);
        } else {
            conditionDao.update(condition);
            MessageUtils.addSuccessMessage("Condition updated successfully!", redirectAttributes);
        }

        return "redirect:/admin/conditions";
    }

}
