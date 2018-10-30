package edu.mit.cci.pogs.model.dao.fileentry.impl;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import edu.mit.cci.pogs.model.dao.api.AbstractDao;
import edu.mit.cci.pogs.model.dao.fileentry.FileEntryDao;
import edu.mit.cci.pogs.model.jooq.tables.pojos.FileEntry;
import edu.mit.cci.pogs.model.jooq.tables.records.FileEntryRecord;

import static edu.mit.cci.pogs.model.jooq.Tables.FILE_ENTRY;

@Repository
public class FileEntryDaoImpl  extends AbstractDao<FileEntry, Long, FileEntryRecord> implements FileEntryDao {

    private final DSLContext dslContext;

    @Autowired
    public FileEntryDaoImpl(DSLContext dslContext){
        super(dslContext, FILE_ENTRY, FILE_ENTRY.ID, FileEntry.class);
        this.dslContext = dslContext;
    }

}
