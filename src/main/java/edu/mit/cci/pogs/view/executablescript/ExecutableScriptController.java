package edu.mit.cci.pogs.view.executablescript;

import edu.mit.cci.pogs.config.AuthUserDetailsService;
import edu.mit.cci.pogs.model.dao.researchgroup.ResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroup;
import edu.mit.cci.pogs.service.ExecutableScriptService;
import edu.mit.cci.pogs.view.executablescript.beans.ExecutableScriptBean;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.dao.executablescript.ScriptType;
import edu.mit.cci.pogs.model.dao.session.SessionScheduleType;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.view.chatscript.beans.ChatScriptBean;

@Controller
public class ExecutableScriptController {

    @Autowired
    private ExecutableScriptDao executableScriptDao;

    @Autowired
    private ExecutableScriptService executableScriptService;

    @Autowired
    private ResearchGroupDao researchGroupDao;

    @GetMapping("/admin/executablescripts")
    public String getExecutableScripts(Model model) {

        model.addAttribute("executablescriptsList", executableScriptDao.listExecutableScriptsWithUserGroup(AuthUserDetailsService.getLoggedInUser()));
        return "executablescript/executablescript-list";
    }

    @ModelAttribute("scriptTypes")
    public List<ScriptType> getScriptTypes() {

        return Arrays.asList(ScriptType.values());
    }

    @GetMapping("/admin/executablescripts/{chatscriptId}")
    public String getExecutableScript(@PathVariable("chatscriptId") Long chatscriptId, Model model) {
        ExecutableScript chatScript = executableScriptDao.get(chatscriptId);
        model.addAttribute("executablescript", chatScript);

        return "executablescript/executablescript-display";
    }

    @GetMapping("admin/executablescripts/create")
    public String createExecutableScript(Model model) {

        ExecutableScriptBean executableScriptBean = new ExecutableScriptBean(new ExecutableScript());

        executableScriptBean.setResearchGroupRelationshipBean(
                new ResearchGroupRelationshipBean());

        model.addAttribute("executablescript",executableScriptBean);

        return "executablescript/executablescript-edit";
    }

    @GetMapping("/admin/executablescripts/{chatscriptId}/edit")
    public String editExecutableScript(@PathVariable("chatscriptId") Long chatscriptId, Model model) {

        ExecutableScriptBean executableScriptBean = new ExecutableScriptBean(executableScriptDao.get(chatscriptId));
        executableScriptBean.setResearchGroupRelationshipBean(
                new ResearchGroupRelationshipBean());
        executableScriptBean.getResearchGroupRelationshipBean()
        .setObjectHasResearchSelectedValues(
                        executableScriptService.listExecutableScriptHasResearchGroupByDictionaryId(chatscriptId));


        model.addAttribute("executablescript", executableScriptBean);
        return "executablescript/executablescript-edit";
    }

    @PostMapping("/admin/executablescripts")
    public String saveExecutableScript(@ModelAttribute ExecutableScriptBean executableScriptBean, RedirectAttributes redirectAttributes) {

        executableScriptService.createOrUpdate(executableScriptBean);

        if (executableScriptBean.getId() == null) {

            MessageUtils.addSuccessMessage("Executable Script created successfully!", redirectAttributes);
        } else {

            MessageUtils.addSuccessMessage("Executable Script updated successfully!", redirectAttributes);
        }
        return "redirect:/admin/executablescripts/"+executableScriptBean.getId();
    }

    @ModelAttribute("researchGroups")
    public List<ResearchGroup> getAllResearchGroups() {

        List<ResearchGroup> res = researchGroupDao.list();
        return res;
    }

}
