package oal.oracle.apps.misegp.drm.service;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import javax.persistence.Query;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.MediaType;

import oal.oracle.apps.misegp.drm.entities.OalegoDrmDataChange;
import oal.oracle.apps.misegp.drm.entities.OalegoDrmSyncData;
import oal.oracle.apps.misegp.drm.queries.DBQueries;


@Stateless
@Path("categoryChange")
@SuppressWarnings("oracle.jdeveloper.webservice.rest.broken-resource-error")
public class CategoryChangeService extends CategoryChangeFacade<OalegoDrmDataChange> {
    public CategoryChangeService() {
        super(OalegoDrmDataChange.class);
    }

    @PersistenceContext(unitName = "Model")
    private EntityManager em;

    @GET
    @Path("/getChangedCategories")
    @Produces("application/json")
    public List<OalegoDrmDataChange> getCategories(@QueryParam("offset") @DefaultValue("0") Integer offset,
                                      @QueryParam("limit") @DefaultValue("100") Integer limit,@QueryParam("catalogCode") String catalogCode
                                      ,@QueryParam("levl") String levl,@QueryParam("refresh_id") String refresh_id                              
                                                                  ) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("catalogCode",catalogCode);
        map.put("levl",levl);
        map.put("refresh_id",refresh_id);
        List<OalegoDrmDataChange> results = getResultsByCriteria(map, offset, limit);
        return results;
    }
    
    @GET
    @Path("/getCurrentRefreshId")
    @Produces("application/text")
    public String getCurrentRefreshId()
    {
            String query = "select max(refresh_id) from "+DBQueries.getDeltaTable()+"";
            Query nativeQuery = getEntityManager().createNativeQuery(query);
            Object result= nativeQuery.getSingleResult();
            
            return String.valueOf(result);
            
    }
    
    @PUT
    @Path("/deleteProcessed")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes("text/plain")
    public String deleteProcessed(String categoryCodes, @QueryParam("refreshId") BigDecimal refreshId, @QueryParam("catalogCode") String catalogCode ) {
        String query = "delete from "+DBQueries.getDeltaTable()+" where refresh_id='"+refreshId+"' and catalog_code ='"+catalogCode+"' and category_code in (%s)";
        String formattedQuery = String.format(query,categoryCodes);
        System.err.println(formattedQuery);
        Query nativeQuery = getEntityManager().createNativeQuery(formattedQuery);
        nativeQuery.executeUpdate();
        
        return "Success";
    }
    
    
    //    @POST
    //    @Path("/getProcessedCategoryNumber")
    //    @Produces("application/text")
    //    public String getProcessedCategoryNumber(@QueryParam("catalogCode") String catalogCode,
    //    ArrayList<String> catIds )
    //    {
    //            String query = DBQueries.GET_NO_OF_CATEGORY_WITH_PROCESSED_FLAG;
    //            String cat = String.join(",", catIds);
    //            String formattedQuery = String.format(query,catalogCode,cat);
    //            Query nativeQuery = getEntityManager().createNativeQuery(formattedQuery, OalegoDrmSyncData.class.getSimpleName());
    //            List<Object[]> result= nativeQuery.getResultList();
    //
    //            return result.get(0)[0].toString();
    //
    //    }
    
   
    @Override
    protected EntityManager getEntityManager() {
        // TODO Implement this method
        return em;
    }
}
