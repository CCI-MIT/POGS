package edu.mit.cci.pogs.model.dao.api;

import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;

/**
 * This class provides a basis for a Dao that provides CRUD functionality.
 *
 * @param <PojoT> The pojo type.
 * @param <IdT> The type of the ID field.
 * @param <RecordT> The corresponding record type.
 */
public abstract class AbstractDao<PojoT, IdT, RecordT extends UpdatableRecord> implements Dao<PojoT, IdT> {

    private final DSLContext dslContext;
    private final Table<RecordT> table;
    private final TableField<RecordT, IdT> idField;
    private final Class<PojoT> pojoClass;

    protected AbstractDao(DSLContext dslContext, Table<RecordT> table,
            TableField<RecordT, IdT> idField, Class<PojoT> pojoClass) {
        this.dslContext = dslContext;
        this.table = table;
        this.idField = idField;
        this.pojoClass = pojoClass;
    }

    private RecordT newRecord() {
        return dslContext.newRecord(table);
    }

    public PojoT get(IdT id) {
        return dslContext.selectFrom(table)
                         .where(idField.eq(id))
                         .fetchOne().into(pojoClass);
    }

    public void update(PojoT pojo) {
        final RecordT record = newRecord();
        record.from(pojo);
        record.update();
    }

    public PojoT create(PojoT pojo) {
        final RecordT record = newRecord();
        record.from(pojo);
        record.store();
        return pojo;
    }

    public void delete(PojoT pojo) {
        final RecordT record = newRecord();
        record.from(pojo);
        record.delete();
    }
}
