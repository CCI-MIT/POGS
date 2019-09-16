package edu.mit.cci.pogs.service;


import edu.mit.cci.pogs.model.dao.executablescript.ExecutableScriptDao;
import edu.mit.cci.pogs.model.dao.executablescripthasresearchgroup.ExecutableScriptHasResearchGroupDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScript;
import edu.mit.cci.pogs.model.jooq.tables.pojos.ExecutableScriptHasResearchGroup;
import edu.mit.cci.pogs.service.base.ServiceBase;
import edu.mit.cci.pogs.utils.ObjectUtils;
import edu.mit.cci.pogs.view.executablescript.beans.ExecutableScriptBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExecutableScriptService extends ServiceBase {

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

        List<Long> toCreate = new ArrayList<>();
        List<Long> toDelete = new ArrayList<>();
        List<ExecutableScriptHasResearchGroup> currentlySelected = listExecutableScriptHasResearchGroupByDictionaryId(executableScriptBean.getId());

        List<Long> currentResearchGroups = currentlySelected
                .stream()
                .map(ExecutableScriptHasResearchGroup::getResearchGroupId)
                .collect(Collectors.toList());

        String[] newSelectedValues = executableScriptBean.getResearchGroupRelationshipBean().getSelectedValues();

        UpdateResearchGroups(toCreate, toDelete, currentResearchGroups, newSelectedValues);

        for (Long toCre : toCreate) {
            ExecutableScriptHasResearchGroup rghau = new ExecutableScriptHasResearchGroup();
            rghau.setExecutableScriptId(executableScriptBean.getId());
            rghau.setResearchGroupId(toCre);
            executableScriptHasResearchGroupDao.create(rghau);
        }
        for (Long toDel : toDelete) {

            ExecutableScriptHasResearchGroup rghau = currentlySelected
                    .stream()
                    .filter(a -> (a.getExecutableScriptId() == executableScriptBean.getId() && a.getResearchGroupId() == toDel))
                    .findFirst().get();

            executableScriptHasResearchGroupDao.delete(rghau);
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
