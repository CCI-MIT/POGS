package edu.mit.cci.pogs.view.todoentry;

import edu.mit.cci.pogs.model.dao.todoentry.TodoEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntry;
import edu.mit.cci.pogs.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/admin/todoentry")
public class TodoEntryController {

    @Autowired
    private TodoEntryDao todoEntryDao;

    @ModelAttribute("researchGroups")
    @GetMapping
    public String getAllTodoEntries(Model model) {
       model.addAttribute("studiesList", todoEntryDao.get());
       return "study/study-list";
    }

    @PostMapping
    public String saveTodoEntry(@ModelAttribute TodoEntry todoEntry, RedirectAttributes redirectAttributes) {

        if(todoEntry.getId() == null){
            todoEntryDao.create(todoEntry);
            MessageUtils.addSuccessMessage("Todo entry created successfully!",redirectAttributes);
        } else {
            todoEntryDao.update(todoEntry);
            MessageUtils.addSuccessMessage("Todo entry updated successfully!",redirectAttributes);
        }
        return "redirect:/admin/todoentry";
    }
}
