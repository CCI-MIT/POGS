package edu.mit.cci.pogs.view.workspace;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import edu.mit.cci.pogs.model.jooq.tables.pojos.IndividualSubjectScore;
;
import edu.mit.cci.pogs.service.IndividualSubjectScoreService;

@RestController
public class WorkspaceRestController {

    @Autowired
    private IndividualSubjectScoreService individualSubjectScoreService;

    @RequestMapping(value = "/individualScoreValues/{completedTaskId}", method = {RequestMethod.GET})
    public List<IndividualSubjectScore> getScoreValues(@PathVariable("completedTaskId") Long completedTaskId) {

        List<IndividualSubjectScore> subjectScores = individualSubjectScoreService.getIndividualScores(completedTaskId);
        if(subjectScores == null){
            return new ArrayList<>();
        }
        return subjectScores;
    }
}
