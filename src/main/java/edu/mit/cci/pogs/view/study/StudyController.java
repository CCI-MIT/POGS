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

import edu.mit.cci.pogs.model.dao.study.StudyDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;
import edu.mit.cci.pogs.utils.MessageUtils;

@Controller
@RequestMapping(value = "/admin/studies")
public class StudyController {

    @Autowired
    private StudyDao studyDao;


    @GetMapping
    public String getStudy(Model model) {

        model.addAttribute("studiesList", studyDao.listStudiesWithUserGroup());
        return "study/study-list";
    }

    @GetMapping("{studyId}")
    public String getStudies(@PathVariable("studyId") Long studyId, Model model) {

        model.addAttribute("study", studyDao.get(studyId));
        return "study/study-display";
    }

    @GetMapping("/create")
    public String createStudy(Model model) {

        model.addAttribute("study", new Study());
        return "study/study-edit";
    }

    @GetMapping("{studyId}/edit")
    public String createStudy(@PathVariable("studyId") Long studyId, Model model) {
        model.addAttribute("study", studyDao.get(studyId));
        return "study/study-edit";
    }

    @PostMapping
    public String saveStudy(@ModelAttribute Study study, RedirectAttributes redirectAttributes) {

        if (study.getId() == null) {
            studyDao.create(study);
            MessageUtils.addSuccessMessage("Study created successfully!",redirectAttributes);
        }else{
            studyDao.update(study);
            MessageUtils.addSuccessMessage("Study updated successfully!",redirectAttributes);
        }

        return "redirect:/admin/studies";
    }

}
