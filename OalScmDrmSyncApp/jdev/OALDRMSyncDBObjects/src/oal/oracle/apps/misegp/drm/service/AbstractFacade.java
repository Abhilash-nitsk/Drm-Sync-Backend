package oal.oracle.apps.misegp.drm.service;

import javax.persistence.EntityManager;
import oal.oracle.apps.misegp.drm.helpers.DBQueryBuilder;


public abstract class AbstractFacade<T> {

    protected Class<T> entityClass;
    protected DBQueryBuilder<T> queryBuilder;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.queryBuilder = new DBQueryBuilder<T>();
    }
    
    
    
    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }
}
