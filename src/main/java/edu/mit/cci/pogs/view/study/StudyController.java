package edu.mit.cci.pogs.view.study;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.config.AuthUserDetailsService;
import edu.mit.cci.pogs.model.dao.researchgroup.ResearchGroupDao;
import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroup;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Session;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.service.StudyService;
import edu.mit.cci.pogs.utils.MessageUtils;
import edu.mit.cci.pogs.view.researchgroup.beans.ResearchGroupRelationshipBean;
import edu.mit.cci.pogs.view.study.beans.StudyBean;

@Controller
@RequestMapping(value = "/admin/studies")
public class StudyController {

    @Autowired
    private StudyDao studyDao;

    @Autowired
    private ResearchGroupDao researchGroupDao;

    @Autowired
    private StudyService studyService;

    @GetMapping
    public String getStudy(Model model) {

        model.addAttribute("studiesList", studyDao.listStudiesWithUserGroup(AuthUserDetailsService.getLoggedInUser()));
        return "study/study-list";
    }

    @GetMapping("{studyId}")
    public String getStudies(@PathVariable("studyId") Long studyId, Model model) {
        Study study = studyDao.get(studyId);
        model.addAttribute("study", study);


        List<Session> sessions = studyService.listSessionsByStudyId(study.getId());

        model.addAttribute("sessionsList", sessions);

        return "study/study-display";
    }

    @GetMapping("/create")
    public String createStudy(Model model) {

        StudyBean sb = new StudyBean();
        sb.setResearchGroupRelationshipBean(
                new ResearchGroupRelationshipBean());

        model.addAttribute("study", sb);
        return "study/study-edit";
    }

    @GetMapping("{studyId}/edit")
    public String createStudy(@PathVariable("studyId") Long studyId, Model model) {

        StudyBean sb = new StudyBean(studyDao.get(studyId));
        sb.setResearchGroupRelationshipBean(
                new ResearchGroupRelationshipBean());
        sb.getResearchGroupRelationshipBean()
                .setStudyHasResearchSelectedValues(
                        studyService.listStudyHasResearchGroupByStudyId(studyId));

        model.addAttribute("study", sb);
        return "study/study-edit";
    }

    @PostMapping
    public String saveStudy(@ModelAttribute StudyBean study, RedirectAttributes redirectAttributes) {

        studyService.createOrUpdate(study);
        if (study.getId() == null) {
            MessageUtils.addSuccessMessage("Study created successfully!", redirectAttributes);
        } else {
            MessageUtils.addSuccessMessage("Study updated successfully!", redirectAttributes);
        }

        return "redirect:/admin/studies";
    }

    @ModelAttribute("researchGroups")
    public List<ResearchGroup> getAllResearchGroups() {

        return researchGroupDao.list();
    }

}
