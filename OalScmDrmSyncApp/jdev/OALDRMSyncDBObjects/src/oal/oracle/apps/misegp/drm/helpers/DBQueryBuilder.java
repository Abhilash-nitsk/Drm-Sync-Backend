package oal.oracle.apps.misegp.drm.helpers;

import java.util.Collections;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class DBQueryBuilder<T> {
    
    public DBQueryBuilder() {
    }

    public Predicate[] getPredicatesByCriterias(Map<String, Object> criterias, 
                                                CriteriaBuilder criteriaBuilder,
                                                Root<?> root) {
        Predicate[] predicates = new Predicate[criterias.size()];

        int i = 0;
        for (Map.Entry<String, Object> criteria : criterias.entrySet()) {
            
            
            if(criteria.getKey().startsWith("-"))
            {
                predicates[i] = criteriaBuilder.notEqual(root.get(criteria.getKey().substring(1)), criteria.getValue());
            }
            else if(criteria.getKey().startsWith(">"))
            {
                predicates[i] = criteriaBuilder.greaterThan(root.get(criteria.getKey().substring(1)), Integer.parseInt(criteria.getValue().toString()));
            }
            else
                predicates[i] = criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            ++i;
        }
        
        return predicates;
    }

    @SuppressWarnings("unchecked")
    public CriteriaQuery createCriteriaQuery(Map<String, Object> criterias, 
                                             CriteriaBuilder criteriaBuilder,
                                             Class entityClass) {
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        Root<T> root = criteriaQuery.from(entityClass);
        criteriaQuery.select(root);

        criterias.values().removeAll(Collections.singleton(null));
        Predicate[] predicates = getPredicatesByCriterias(criterias, criteriaBuilder, root);

        criteriaQuery.where(predicates);

        return criteriaQuery;
    }
}
