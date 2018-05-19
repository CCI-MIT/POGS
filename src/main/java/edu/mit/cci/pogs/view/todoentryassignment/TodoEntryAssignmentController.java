package edu.mit.cci.pogs.view.todoentryassignment;

import edu.mit.cci.pogs.model.dao.todoentryassignment.TodoEntryAssignmentDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TodoEntryAssignment;
import edu.mit.cci.pogs.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/todoentryassignments")
public class TodoEntryAssignmentController {

    @Autowired
    private TodoEntryAssignmentDao todoEntryAssignmentDao;

    @ModelAttribute("researchGroups")
    public String getAllTodoEntryAssignments(Model model) {
        model.addAttribute("studiesList", todoEntryAssignmentDao.get());
        return "study/study-list";
    }

    @PostMapping
    public String saveTodoEntryAssignment(@ModelAttribute TodoEntryAssignment todoEntryAssignment, RedirectAttributes redirectAttributes) {

        if(todoEntryAssignment.getId() == null){
            todoEntryAssignmentDao.create(todoEntryAssignment);
            MessageUtils.addSuccessMessage("Todo entry Assignment created successfully!",redirectAttributes);
            } else {
            todoEntryAssignmentDao.update(todoEntryAssignment);
            MessageUtils.addSuccessMessage("Todo entry Assignment updated successfully!",redirectAttributes);
            }
        return "redirect:/admin/todoentry";
    }
}
