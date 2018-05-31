package edu.mit.cci.pogs.view.todoentry;

import edu.mit.cci.pogs.model.dao.todoentry.TodoEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntry;
import edu.mit.cci.pogs.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/admin/todoentries")
public class TodoEntryController {

    @Autowired
    private TodoEntryDao todoEntryDao;

    @GetMapping
    public String getAllTodoEntries(Model model) {
       model.addAttribute("todoentryList", todoEntryDao.list());
       return "todo/todoEntry-list";
    }

    @GetMapping("/create")
    public String createTodoEntry(Model model) {

        TodoEntry todoEntry = new TodoEntry();
        model.addAttribute("todoentry", todoEntry);
        return "todoentry/todoentry-edit";
    }

    @GetMapping("{todoEntryId}/edit")
    public String editTodoEntry(@PathVariable("todoEntryId") Long todoEntryId, Model model) {

        TodoEntry todoEntry = new TodoEntry(todoEntryDao.get(todoEntryId));
        model.addAttribute("todoentry", todoEntry);
        return "todoentry/todoentry-edit";
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
