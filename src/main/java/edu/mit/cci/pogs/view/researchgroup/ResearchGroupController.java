package edu.mit.cci.pogs.view.researchgroup;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import edu.mit.cci.pogs.model.dao.researchgroup.ResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ResearchGroup;
import edu.mit.cci.pogs.utils.MessageUtils;
 
@Controller
@RequestMapping(value = "/admin/researchgroups")
public class ResearchGroupController {
 
    @Autowired
    private ResearchGroupDao researchGroupDao;
 
    @GetMapping
    public String getResearchGroup(Model model) {
 
        model.addAttribute("researchGroupsList", researchGroupDao.list());
        return "researchgroup/researchgroup-list";
    }
 
    @GetMapping("{id}")
    public String getResearchGroups(@PathVariable("id") Long id, Model model) {
 
        model.addAttribute("researchGroup", researchGroupDao.get(id));
        return "researchgroup/researchgroup-display";
    }
 
    @GetMapping("/create")
    public String createResearchGroup(Model model) {
 
        model.addAttribute("researchGroup", new ResearchGroup());
        return "researchgroup/researchgroup-edit";
    }
 
    @GetMapping("{id}/edit")
    public String createResearchGroup(@PathVariable("id") Long id, Model model) {
        model.addAttribute("researchGroup", researchGroupDao.get(id));
        return "researchgroup/researchgroup-edit";
    }
 
    @PostMapping
    public String saveResearchGroup(@ModelAttribute ResearchGroup researchGroup, RedirectAttributes redirectAttributes) {
 
        if (researchGroup.getId() == null) {
            researchGroupDao.create(researchGroup);
            MessageUtils.addSuccessMessage("ResearchGroup created successfully!",redirectAttributes);
        }else{
            researchGroupDao.update(researchGroup);
            MessageUtils.addSuccessMessage("ResearchGroup updated successfully!",redirectAttributes);
        }
 
        return "redirect:/admin/researchgroups";
    }
 
}
