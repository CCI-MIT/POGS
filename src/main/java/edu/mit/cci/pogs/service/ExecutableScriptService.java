package edu.mit.cci.pogs.service;


import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.dao.executablescripthasresearchgroup.ExecutableScriptHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScriptHasResearchGroup;
import edu.mit.cci.pogs.utils.ObjectUtils;
import edu.mit.cci.pogs.view.executablescript.beans.ExecutableScriptBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExecutableScriptService {

    private final ExecutableScriptHasResearchGroupDao executableScriptHasResearchGroupDao;
    private final ExecutableScriptDao executableScriptDao;


    @Autowired
    public ExecutableScriptService(ExecutableScriptHasResearchGroupDao executableScriptHasResearchGroupDao, ExecutableScriptDao executableScriptDao) {
        this.executableScriptHasResearchGroupDao = executableScriptHasResearchGroupDao;
        this.executableScriptDao = executableScriptDao;
    }


    public List<ExecutableScriptHasResearchGroup> listExecutableScriptHasResearchGroupByDictionaryId(Long executableScriptId) {
        return this.executableScriptHasResearchGroupDao.listByExecutableScriptId(executableScriptId);
    }


    private void createOrUpdateUserGroups(ExecutableScriptBean executableScriptBean) {
        if (executableScriptBean.getResearchGroupRelationshipBean() == null && executableScriptBean.getResearchGroupRelationshipBean().getSelectedValues() == null) {
            return;
        }
        List<ExecutableScriptHasResearchGroup> toCreate = new ArrayList<>();
        List<ExecutableScriptHasResearchGroup> toDelete = new ArrayList<>();
        List<ExecutableScriptHasResearchGroup> currentlySelected = listExecutableScriptHasResearchGroupByDictionaryId(executableScriptBean.getId());

        for (ExecutableScriptHasResearchGroup rghau : currentlySelected) {
            boolean foundRGH = false;
            for (String researchGroupId : executableScriptBean.getResearchGroupRelationshipBean().getSelectedValues()) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    foundRGH = true;
                }
            }
            if (!foundRGH) {
                toDelete.add(rghau);
            }

        }


        for (String researchGroupId : executableScriptBean.getResearchGroupRelationshipBean().getSelectedValues()) {

            boolean selectedAlreadyIn = false;
            for (ExecutableScriptHasResearchGroup rghau : currentlySelected) {
                if (rghau.getResearchGroupId().longValue() == new Long(researchGroupId).longValue()) {
                    selectedAlreadyIn = true;
                }
            }
            if (!selectedAlreadyIn) {
                ExecutableScriptHasResearchGroup rghau = new ExecutableScriptHasResearchGroup();
                rghau.setExecutableScriptId(executableScriptBean.getId());
                rghau.setResearchGroupId(new Long(researchGroupId));
                toCreate.add(rghau);
            }

        }
        for (ExecutableScriptHasResearchGroup toCre : toCreate) {
            executableScriptHasResearchGroupDao.create(toCre);
        }
        for (ExecutableScriptHasResearchGroup toDel : toDelete) {
            executableScriptHasResearchGroupDao.delete(toDel);
        }

    }


    public ExecutableScript createOrUpdate(ExecutableScriptBean executableScriptBean) {

        ExecutableScript executableScript = new ExecutableScript();

        ObjectUtils.Copy(executableScript, executableScriptBean);

        if (executableScript.getId() == null) {
            executableScript = executableScriptDao.create(executableScript);
            executableScriptBean.setId(executableScript.getId());
            createOrUpdateUserGroups(executableScriptBean);
        } else {
            executableScriptDao.update(executableScript);
            createOrUpdateUserGroups(executableScriptBean);
        }
        return executableScript;

    }

}
