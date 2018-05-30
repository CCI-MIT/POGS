package edu.mit.cci.pogs.view.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.mit.cci.pogs.model.dao.researchgroup.ResearchGroupDao;
import edu.mit.cci.pogs.model.dao.task.ScoringType;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Task;
import edu.mit.cci.pogs.service.TaskService;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.utils.SqlTimestampPropertyEditor;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;
import edu.mit.cci.pogs.view.task.bean.TaskBean;

@Controller
@RequestMapping(value = "/admin/tasks")
public class TaskController {

    @Autowired
    private TaskDao taskDao;

    @Autowired
    private ResearchGroupDao researchGroupDao;


    @Autowired
    private TaskService taskService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Timestamp.class, new SqlTimestampPropertyEditor());
    }

    @ModelAttribute("scoringTypes")
    public List<ScoringType> getTeamCreationMethods() {
        return Arrays.asList(ScoringType.values());
    }

    @ModelAttribute("taskPluginTypes")
    private List<TaskPlugin> getTaskPlugins() {
        List<TaskPlugin> rt = new ArrayList<>();

        rt.add(new TaskPlugin("Sudoku", false));
        rt.add(new TaskPlugin("Multiple choice questions", false));

        return rt;
    }

    @GetMapping
    public String getTask(Model model) {

        model.addAttribute("tasksList", taskDao.list());
        return "task/task-list";
    }

    @GetMapping("{id}")
    public String getTasks(@PathVariable("id") Long id, Model model) {

        model.addAttribute("task", taskDao.get(id));
        return "task/task-display";
    }

    @GetMapping("/create")
    public String createTask(Model model) {
        TaskBean tb = new TaskBean();

        tb.setResearchGroupRelationshipBean(
                new ResearchGroupRelationshipBean());
        model.addAttribute("task", tb);
        return "task/task-edit";
    }

    @GetMapping("{id}/edit")
    public String createTask(@PathVariable("id") Long id, Model model) {
        TaskBean tb = new TaskBean(taskDao.get(id));

        tb.setResearchGroupRelationshipBean(
                new ResearchGroupRelationshipBean());
        tb.getResearchGroupRelationshipBean()
                .setTaskyHasResearchSelectedValues(
                        taskService.listTaskHasResearchGroupByTaskId(id));

        model.addAttribute("task", tb);
        return "task/task-edit";
    }

    @PostMapping
    public String saveTask(@ModelAttribute TaskBean task, RedirectAttributes redirectAttributes) {

        taskService.createOrUpdate(task);
        if (task.getId() == null) {
            MessageUtils.addSuccessMessage("Task created successfully!", redirectAttributes);
        } else {
            MessageUtils.addSuccessMessage("Task updated successfully!", redirectAttributes);
        }

        return "redirect:/admin/tasks";
    }

    @ModelAttribute("researchGroups")
    public List<ResearchGroup> getAllResearchGroups() {

        return researchGroupDao.list();
    }

}
