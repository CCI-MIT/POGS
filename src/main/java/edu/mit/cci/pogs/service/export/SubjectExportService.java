package edu.mit.cci.pogs.service.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import edu.mit.cci.pogs.model.dao.round.RoundDao;
import edu.mit.cci.pogs.model.dao.subject.SubjectDao;
import edu.mit.cci.pogs.model.dao.subjectattribute.SubjectAttributeDao;
import edu.mit.cci.pogs.model.dao.team.TeamDao;
import edu.mit.cci.pogs.model.dao.teamhassubject.TeamHasSubjectDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Round;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Subject;
import edu.mit.cci.pogs.model.jooq.tables.pojos.SubjectAttribute;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Team;
import edu.mit.cci.pogs.model.jooq.tables.pojos.TeamHasSubject;
import edu.mit.cci.pogs.service.export.exportBeans.ExportFile;
import edu.mit.cci.pogs.service.export.exportBeans.SubjectExport;
import edu.mit.cci.pogs.utils.ExportUtils;

@Service
public class SubjectExportService {

    @Autowired
    private TeamDao teamDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private SubjectAttributeDao subjectAttributeDao;

    @Autowired
    private TeamHasSubjectDao teamHasSubjectDao;

    @Autowired
    private RoundDao roundDao;

    public List<ExportFile> getSubjectAndTeamsFiles(Long sessionId, Long studyId, String path) {


        List<Round> rounds = roundDao.listBySessionId(sessionId);
        List<SubjectExport> subjectExports = new ArrayList<>();
        List<ExportFile> exportFiles = new ArrayList<>();
        for(Round round: rounds) {
            List<Team> teams = teamDao.listByRoundId(round.getId());

            for(Team team : teams){
                List<TeamHasSubject> teamHasSubjects = teamHasSubjectDao.listByTeamId(team.getId());
                for(TeamHasSubject ths : teamHasSubjects) {
                    Subject su = subjectDao.get(ths.getSubjectId());
                    SubjectExport se = new SubjectExport(su);
                    se.setSubjectTeam(team.getId().toString());
                    subjectExports.add(se);
                    List<SubjectAttribute> subjectAttributes = subjectAttributeDao
                            .listBySubjectId(su.getId());

                    exportFiles.addAll(ExportUtils.getEntityDataExportFile(path, SubjectAttribute.class,
                            subjectAttributes, studyId, sessionId, null, su.getId()));
                }
            }
        }

        exportFiles.addAll(ExportUtils.getEntityDataExportFile(path, SubjectExport.class,subjectExports,
                studyId,sessionId,  null,null));

        return exportFiles;
    }

}
