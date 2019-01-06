package oal.oracle.apps.misegp.drm.service;

import java.lang.reflect.Field;

import java.math.BigDecimal;

import java.util.ArrayList;
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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.MediaType;

import oal.oracle.apps.misegp.drm.entities.OalegoDrmSyncData;


import oal.oracle.apps.misegp.drm.queries.DBQueries;

import org.json.JSONObject;

import oal.oracle.apps.misegp.drm.utils.StringUtils;

import org.json.JSONArray;

@Stateless
@Path("category")
@SuppressWarnings("oracle.jdeveloper.webservice.rest.broken-resource-error")
public class CategoryService extends CategoryFacade<OalegoDrmSyncData> {
    public CategoryService() {
        super(OalegoDrmSyncData.class);
    }

    @PersistenceContext(unitName = "Model")
    private EntityManager em;

    @GET
    @Path("/getCategories")
    @Produces("application/json")
    public List<OalegoDrmSyncData> getCategories(@QueryParam("offset") @DefaultValue("0") Integer offset,
                                      @QueryParam("limit") @DefaultValue("100") Integer limit) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<OalegoDrmSyncData> results = getResultsByCriteria(map, offset, limit);
        return results;
    }
    
    //Rest API to Post the Categories in the PAAS Staging table 
    @POST
    @Path("/postCategories")
    @Produces("application/text")
    @Consumes("application/json")
    public String postCategoriesList(OalegoDrmSyncData[] testCategories, @QueryParam("refreshId") BigDecimal refreshId, @QueryParam("catalogCode") String catalogCode ) {
        return postCategories(testCategories, refreshId, catalogCode);
    }
    
    //Rest API to update the categories from the paas DB
    @PUT
    @Path("/updateProcessed")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes("application/json")
    public String updateProcessed(OalegoDrmSyncData[] testCategories) {
        return updateProcessedHelper(testCategories);
    }
    
    //Rest API to update the categories from the paas DB
    @PUT
    @Path("/updateProcessedFlag")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes("text/plain")
    public String updateProcessedFlag(String categoryCodes, @QueryParam("refreshId") BigDecimal refreshId, @QueryParam("catalogCode") String catalogCode, @QueryParam("processedFlag") String processedFlag ) {
       // String query = "update OALEGO_DRM_SYNC_DATA set processed_flag='"+processedFlag+"' where refresh_id='"+refreshId+"' and catalog_code ='"+catalogCode+"' and category_code in (%s)";
        String query=DBQueries.updateProcessedFlag(processedFlag, String.valueOf(refreshId), catalogCode);
        String formattedQuery = String.format(query,categoryCodes);
        System.err.println(formattedQuery);
        Query nativeQuery = getEntityManager().createNativeQuery(formattedQuery);
        nativeQuery.executeUpdate();
        return "Success";
    }
    
    @PUT
    @Path("/updateComments")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes("text/plain")
    public String updateComments(String comments, @QueryParam("refreshId") BigDecimal refreshId, @QueryParam("catalogCode") String catalogCode, @QueryParam("categoryCode") String categoryCode ) {
       // String query = "update OALEGO_DRM_SYNC_DATA set processed_flag='"+processedFlag+"' where refresh_id='"+refreshId+"' and catalog_code ='"+catalogCode+"' and category_code in (%s)";
        String formattedQuery=DBQueries.updateComments(comments,refreshId,catalogCode,categoryCode);
        System.err.println(formattedQuery);
        Query nativeQuery = getEntityManager().createNativeQuery(formattedQuery);
        nativeQuery.executeUpdate();
        return "Successully updated comments!";
    }
    
   
    //Rest API to find the given category from the paas DB
    @GET
    @Path("/findCategory")
    @Produces("application/json")
    public List<OalegoDrmSyncData> findCategories(@QueryParam("offset") @DefaultValue("0") Integer offset,
                                      @QueryParam("limit") @DefaultValue("100") Integer limit,@QueryParam("categoryCode") String categoryCode) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("categoryCode",categoryCode);
        List<OalegoDrmSyncData> results = getResultsByCriteria(map, offset, limit);
        return results;
    }
    
    @GET
    @Path("/getCategoryCount")
    @Produces("application/json")
    public String getCategoryCount(@QueryParam("catalogCode") String catalogCode
                                      ,@QueryParam("levl") String levl,@QueryParam("refreshId") String refreshId,@QueryParam("processedFlag") String processedFlag                              
                                                                  ) {
        //String query = "select count(*) from OALEGO_DRM_SYNC_DATA where processed_flag='"+processedFlag+"' and refresh_id='"+refreshId+"' and catalog_code ='"+catalogCode+"' and levl='"+levl+"'";
        String query=DBQueries.getCategoryCount(processedFlag, refreshId, catalogCode, levl);
        
        System.err.println(query);
        Query nativeQuery = getEntityManager().createNativeQuery(query);
        Object result= nativeQuery.getSingleResult();
        return String.valueOf(result);
    }
    
    //REST API to get all the categories based on the given catalog
    @GET
    @Path("/getCategoryCatalog")
    @Produces("application/json")
    public List<OalegoDrmSyncData> getCategoryCatalog(@QueryParam("offset") @DefaultValue("0") Integer offset,
                                      @QueryParam("limit") @DefaultValue("100") Integer limit,@QueryParam("catalogCode") String catalogCode
                                      ,@QueryParam("levl") String levl,@QueryParam("refreshId") String refreshId,@QueryParam("processedFlag") String processedFlag                              
                                                                  ) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("catalogCode",catalogCode);
        map.put("levl",levl);
        map.put("processedFlag",processedFlag);
        map.put("refreshId",refreshId);
        
        
        List<OalegoDrmSyncData> results = getResultsByCriteria(map, offset, limit);
        return results;
    }
    
    @GET
    @Path("/getMaxLevel")
    @Produces("application/text")
    public String getMaxlevel(@QueryParam("catalogCode") String catalogCode)
    {
            String query = DBQueries.GET_MAX_LEVEL_FROM_CATALOG;
            String formattedQuery = String.format(query,catalogCode);
            Query nativeQuery = getEntityManager().createNativeQuery(formattedQuery, OalegoDrmSyncData.class.getSimpleName());
            List<Object[]> result= nativeQuery.getResultList();
            try{
            return result.get(0)[0].toString();
                
            }catch(Exception e) {
                e.printStackTrace();
                return "0";
            }
            
    }
    
    
    
    @POST
    @Path("/getNoOfProcessedCatgories")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String getNoOfProcessedCatgories(String categoryCodes, @QueryParam("refreshId") BigDecimal refreshId, @QueryParam("catalogCode") String catalogCode ) {
       // String query = "select count(*) from OALEGO_DRM_SYNC_DATA where processed_flag='P' and refresh_id='"+refreshId+"' and catalog_code ='"+catalogCode+"' and category_code in (%s)";
        String query = DBQueries.getNoofcategories(String.valueOf(refreshId), catalogCode);
        String formattedQuery = String.format(query,categoryCodes);
        System.err.println(formattedQuery);
        Query nativeQuery = getEntityManager().createNativeQuery(formattedQuery);
        Object result= nativeQuery.getSingleResult();
        return String.valueOf(result);
    }
    
    @POST
    @Path("/getUnProcessedORErroredCatgories")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String getUnProcessedORErroredCatgories(String categoryCodes, @QueryParam("refreshId") BigDecimal refreshId, @QueryParam("catalogCode") String catalogCode ) {
        String query = DBQueries.getUnprocessedOrErroredCat( refreshId,catalogCode);
            //String query = DBQueries.getNoofcategories(String.valueOf(refreshId), catalogCode);
         String formattedQuery = String.format(query,categoryCodes);
        System.err.println(formattedQuery);
        Query nativeQuery = getEntityManager().createNativeQuery(formattedQuery);
        Object result= nativeQuery.getSingleResult();
        return String.valueOf(result);
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
    
    @GET
    @Path("/getMostRecentRefresh")
    @Produces("application/text")
    public String getMostRecentRefresh(@QueryParam("catalogCode") String catalogCode)
    {
            String query = DBQueries.GET_MOST_RECENT_REFRESH;
            String formattedQuery = String.format(query,catalogCode);
            Query nativeQuery = getEntityManager().createNativeQuery(formattedQuery, OalegoDrmSyncData.class.getSimpleName());
            List<Object[]> result= nativeQuery.getResultList();
            
            return result.get(0)[0].toString();
            
    }
  
    @GET
    @Path("/getNewRefreshId")
    @Produces("application/text")
    public String getNewRefreshId(@QueryParam("catalogCode") String catalogCode)
    {
            String query = DBQueries.GET_NEW_REFRESH_ID;
            String formattedQuery = String.format(query,catalogCode);
            Query nativeQuery = getEntityManager().createNativeQuery(formattedQuery, OalegoDrmSyncData.class.getSimpleName());
            List<Object[]> result= nativeQuery.getResultList();
            
            return result.get(0)[0].toString();
            
    }

    

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
