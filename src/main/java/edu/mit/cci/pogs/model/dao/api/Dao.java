package edu.mit.cci.pogs.model.dao.api;

public interface Dao<PojoT, IdT> {

    PojoT get(IdT id);

    void update(PojoT pojo);

    PojoT create(PojoT pojo);
}
