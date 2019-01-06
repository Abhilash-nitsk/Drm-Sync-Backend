package oal.oracle.apps.misegp.drm.service;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;

import javax.persistence.EntityManager;

import javax.persistence.criteria.CriteriaBuilder;

import javax.persistence.criteria.CriteriaQuery;

import oal.oracle.apps.misegp.drm.entities.OalegoDrmSyncData;


public abstract class CategoryFacade<T> extends AbstractFacade<T> {
    public CategoryFacade(Class<T> entityClass) {
        super(entityClass);
    }

    @SuppressWarnings("unchecked")
    public List<OalegoDrmSyncData> getResultsByCriteria(Map<String, Object> criterias, int offset, int limit) {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteriaQuery = queryBuilder.createCriteriaQuery(criterias, criteriaBuilder, OalegoDrmSyncData.class);

        return getEntityManager().createQuery(criteriaQuery)
                                 .setFirstResult(offset)
                                 .setMaxResults(limit)
                                 .getResultList();
    }
    
    
    public String postCategories(OalegoDrmSyncData[] testCategories, BigDecimal refreshId, String catalogCode) {
    //        List<OalegoDrmSyncData> testCategories = testList.getEntityList();
        System.out.println(testCategories[0].getCatalogCode());
        for(OalegoDrmSyncData testCategory : testCategories){
            testCategory.setProcessedFlag("U");
            testCategory.setRefreshId(refreshId);
            testCategory.setCatalogCode(catalogCode);
            getEntityManager().persist(testCategory);
            getEntityManager().flush();
            getEntityManager().clear();
        }
        return "Success";
    }
    
    public String updateProcessedHelper(OalegoDrmSyncData[] testCategories) {
        //List<OalegoDrmSyncData> testCategories = testList.getEntityList();
        //System.out.println(testCategories[0].getCatalogCode());
        for(OalegoDrmSyncData testCategory : testCategories){
            getEntityManager().merge(testCategory);
            getEntityManager().flush();
            getEntityManager().clear();
        }
        return "Success";
    }
    
    
}
