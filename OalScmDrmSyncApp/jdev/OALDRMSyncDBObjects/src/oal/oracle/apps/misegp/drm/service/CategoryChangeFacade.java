
package oal.oracle.apps.misegp.drm.service;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import oal.oracle.apps.misegp.drm.entities.OalegoDrmDataChange;
import oal.oracle.apps.misegp.drm.entities.OalegoDrmSyncData;

public abstract class CategoryChangeFacade<T> extends AbstractFacade<T> {
    public CategoryChangeFacade(Class<T> entityClass) {
        super(entityClass);
    }


    @SuppressWarnings("unchecked")
    public List<OalegoDrmDataChange> getResultsByCriteria(Map<String, Object> criterias, int offset, int limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = queryBuilder.createCriteriaQuery(criterias, criteriaBuilder, OalegoDrmDataChange.class);

        return getEntityManager().createQuery(criteriaQuery)
                                 .setFirstResult(offset)
                                 .setMaxResults(limit)
                                 .getResultList();
    }

}
