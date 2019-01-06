package oal.oracle.apps.misegp.drm.queries;

import java.math.BigDecimal;
import oal.oracle.apps.scm.drm.DRMSyncPropertyV2;
public class DBQueries {
    
    public static String getStageTable() {
       return DRMSyncPropertyV2.getInstance().getStageTable();
    }
   public static String getDeltaTable() {
        return DRMSyncPropertyV2.getInstance().getDeltaTable();
    }
    
    public static String GET_MAX_LEVEL_FROM_CATALOG = "select max(levl) from "+getStageTable()+" where catalog_code = '%s'";
    public static String GET_MOST_RECENT_REFRESH = "select max(refresh_Id) from "+getStageTable()+" where catalog_code = '%s'";
    public static String GET_NO_OF_CATEGORY_WITH_PROCESSED_FLAG="select  COUNT(*) from "+getStageTable()+" WHERE catalog_code = 'ZBBB' and category_code IN ('%s') AND PROCESSED_FLAG='U'";
    public static String GET_NEW_REFRESH_ID = "select (max(refresh_id)+10) from OALSCM."+getStageTable()+" WHERE catalog_code = '%s'";
    
    public static String getNoofcategories(String refreshId,String catalogCode) {
        return "select count(*) from "+getStageTable()+" where processed_flag='P' and refresh_id='"+refreshId+"' and catalog_code ='"+catalogCode+"' and category_code in (%s)";
        
    }
    
    
    public static String getCategoryCount(String processedFlag,String refreshId,String catalogCode,String levl) {
        return "select count(*) from "+getStageTable()+" "+
        "where processed_flag='"+processedFlag+"' and refresh_id='"+refreshId+"' and catalog_code ='"+catalogCode+"' and levl='"+levl+"'";
                
    }
   
    public static String updateProcessedFlag(String processedFlag,String refreshId,String catalogCode) {
        String query = "update "+getStageTable()+" set processed_flag='"+processedFlag+"' where refresh_id='"+refreshId+"' and catalog_code ='"+catalogCode+"' and category_code in (%s)";
        return query;
    }
    
    
    public DBQueries() {
        super();
    }

    public static String updateComments(String comments, BigDecimal refreshId, String catalogCode, String categoryCode) {
        return "update "+getStageTable()+" set comments='"+comments+"' where refresh_id='"+refreshId+"' and catalog_code='"+catalogCode+"' and category_code='"+categoryCode+"'"; 
    
    }

    public static String getUnprocessedOrErroredCat(BigDecimal refreshId, String catalogCode) {
        return  "select category_code from "+getStageTable()+" where processed_flag!='P' and refresh_id='"+refreshId+"' and catalog_code ='"+catalogCode+"' and category_code in (%s)";
        
      
    }
}
