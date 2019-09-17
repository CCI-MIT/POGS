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

import edu.mit.cci.pogs.config.AuthUserDetailsService;
import edu.mit.cci.pogs.model.dao.chatscript.ChatScriptDao;
import edu.mit.cci.pogs.model.dao.researchgroup.ResearchGroupDao;
import edu.mit.cci.pogs.model.dao.session.CommunicationConstraint;
import edu.mit.cci.pogs.model.dao.task.ScoringType;
import edu.mit.cci.pogs.model.dao.task.TaskDao;
import edu.mit.cci.pogs.model.dao.taskconfiguration.TaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskhastaskconfiguration.TaskHasTaskConfigurationDao;
import edu.mit.cci.pogs.model.dao.taskplugin.TaskPlugin;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ChatScript;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskConfiguration;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TaskHasTaskConfiguration;
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

    @Autowired
    private TaskConfigurationDao taskConfigurationDao;

    @Autowired
    private TaskHasTaskConfigurationDao taskHasTaskConfigurationDao;

    @Autowired
    private ChatScriptDao chatScriptDao;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Timestamp.class, new SqlTimestampPropertyEditor());
    }

    @ModelAttribute("chatScripts")
    public List<ChatScript> getChatScripts(){
        return chatScriptDao.listChatScriptWithUserGroup(AuthUserDetailsService.getLoggedInUser());
    }
    @ModelAttribute("communicationConstraints")
    public List<CommunicationConstraint> getCommunicationConstraints() {
        return Arrays.asList(CommunicationConstraint.values());
    }

    @ModelAttribute("scoringTypes")
    public List<ScoringType> getTeamCreationMethods() {
        return Arrays.asList(ScoringType.values());
    }

    @ModelAttribute("taskPluginConfigurationOptions")
    public List<TaskConfiguration> getPluginConfiguration(){
        List<TaskConfiguration> taskConfigurationList = new ArrayList<>();
        for(TaskPlugin tp : TaskPlugin.getAllTaskPlugins()) {
            taskConfigurationList.addAll(taskConfigurationDao.listTaskConfigurationsByNameWithUserGroup(tp.getTaskPluginName(), AuthUserDetailsService.getLoggedInUser()));
        }
        return taskConfigurationList;
    }

    @ModelAttribute("taskPluginTypes")
    private List<TaskPlugin> getTaskPlugins() {
        return TaskPlugin.getAllTaskPlugins();
    }

    @GetMapping
    public String getTask(Model model) {

        model.addAttribute("tasksList", taskDao.listTasksWithUserGroup(AuthUserDetailsService.getLoggedInUser()));
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
        tb.setShouldScore(false);

        tb.setResearchGroupRelationshipBean(
                new ResearchGroupRelationshipBean());

        model.addAttribute("task", tb);
        return "task/task-edit";
    }

    @GetMapping("{id}/edit")
    public String createTask(@PathVariable("id") Long id, Model model) {
        TaskBean tb = new TaskBean(taskDao.get(id));

        tb.setResearchGroupRelationshipBean(new ResearchGroupRelationshipBean());
        tb.getResearchGroupRelationshipBean().setObjectHasResearchSelectedValues(taskService.listTaskHasResearchGroupByTaskId(id));

        TaskHasTaskConfiguration thtc = taskHasTaskConfigurationDao.getByTaskId(tb.getId());
        tb.setTaskConfigurationId(thtc.getTaskConfigurationId());

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
