package oal.oracle.apps.misegp.drm.service;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;

import javax.persistence.EntityManager;

import javax.persistence.criteria.CriteriaBuilder;

import javax.persistence.criteria.CriteriaQuery;

import oal.oracle.apps.misegp.drm.entities.OalTestItem;

public abstract class ItemFacade<T> extends AbstractFacade<T> {
    public ItemFacade(Class<T> entityClass) {
        super(entityClass);
    }

    @SuppressWarnings("unchecked")
    public List<OalTestItem> getIVTResultsByCriteria(Map<String, Object> criterias, int offset, int limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = queryBuilder.createCriteriaQuery(criterias, criteriaBuilder, OalTestItem.class);

        return getEntityManager().createQuery(criteriaQuery)
                                 .setFirstResult(offset)
                                 .setMaxResults(limit)
                                 .getResultList();
    }

    public String postItems(List<OalTestItem> testItems) {
        for(OalTestItem testItem : testItems){
            getEntityManager().persist(testItem);
            getEntityManager().flush();
            getEntityManager().clear();
        }
        return "Success";
    }
}
