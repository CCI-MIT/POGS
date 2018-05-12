package edu.mit.cci.pogs.model.dao.study;

import java.util.List;

import edu.mit.cci.pogs.model.dao.api.Dao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.Study;

public interface StudyDao extends Dao<Study, Long> {

    List<Study> listStudiesWithUserGroup(Long id);
}


