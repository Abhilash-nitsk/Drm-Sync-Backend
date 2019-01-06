//package oal.oracle.apps.scm.drm;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
////import org.apache.log4j.Logger;
//
//import java.util.Properties;
//
//
///**
// * Singleton Class to get all properties of DRMSyncApplication
// */
//public class DRMSyncProperty {
//    
//    private static DRMSyncProperty drmsyncproperty=null;
//    
//   // private static Logger logger = Logger.getLogger(DRMSyncProperty.class.getName());
//    private  Map<String, String> map =null;
//
//    private String PROPERTY_FILE = "drmsync.properties";
//    private Properties drmSyncProperties = null;
//
//    private String CUSTOM_CATALOG_PROPERTY_FILE = "customCatalogCode.properties";
//    private Properties customCatalogProperties = null;
//
//    private DRMSyncProperty() {
//        
//    }
//    
//    /**
//     * get Single Instance of this class
//     * @return
//     */
//    public static DRMSyncProperty getInstance() {
//        if(drmsyncproperty==null)
//            drmsyncproperty=new DRMSyncProperty();
//        
//        return drmsyncproperty;
//        
//        
//    }
//    
//    private String getCustomCatalogCodeModelResource(String key) {
//            // read property file only once
//            if (customCatalogProperties == null) {
//                initCatalogProperties();
//            }
//     
//            return customCatalogProperties.getProperty(key);
//        }
//     
//        /**
//         * load the properties from the resource file
//         */
//        
//        
//        public void initCatalogProperties() {
//            InputStream asStream = this.getClass().getClassLoader().getResourceAsStream(CUSTOM_CATALOG_PROPERTY_FILE);
//            if (asStream == null) {
//                
//          //      logger.fatal("Could not load property file: '" + CUSTOM_CATALOG_PROPERTY_FILE + "'");
//                customCatalogProperties = new Properties();
//                return;
//            }
//     
//            customCatalogProperties = new Properties();
//            try {
//                customCatalogProperties.load(asStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//               // logger.error("Can't load properties: " + e.getMessage());
//            }
//          //  logger.info("CustomCatalogCodeModel properties loaded!");
//        }
//        
//    public String getModelResource(String key) {
//            // read property file only once
//            if (drmSyncProperties == null) {
//                initProperties();
//            }
//     
//            return drmSyncProperties.getProperty(key);
//        }
//     
//        /**
//         * load the properties from the resource file
//         */
//        public void initProperties() {
//            InputStream asStream = this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE);
//            if (asStream == null) {
//                
//             //   logger.fatal("Could not load property file: '" + PROPERTY_FILE + "'");
//                drmSyncProperties = new Properties();
//                return;
//            }
//     
//            drmSyncProperties = new Properties();
//            try {
//                drmSyncProperties.load(asStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            //    logger.error("Can't load properties: " + e.getMessage());
//            }
//           // logger.info("Model properties loaded!");
//        }
//        
//    public void updateLastRefreshIdToProperty(int refId) throws FileNotFoundException, IOException {
//        drmSyncProperties.setProperty("lastRefreshId", String.valueOf(refId));
//        FileOutputStream out = new FileOutputStream(PROPERTY_FILE);
//        drmSyncProperties.store(out, null);
//        out.close();
//        
//    }
//   
//    
//    public  String getLocalFileStrorageName() {
//        return getCatalogName()+getCatalogCode();
//    }
//    public boolean loadPreviousBuild() {
//        return Boolean.parseBoolean(getModelResource("loadPreviousBuild"));
//    }
//    
//    public String getPrependString() {
//        //return getCustomCatalogCodeModelResource(getCatalogCode());
//        return getCustomCatalogCodeModelResource("prependString");
//    }
//    public void setPrependString(String s) {
//        customCatalogProperties.setProperty("prependString", s);
//    }
//    
//    public String getCurrentRefreshId() {
//        return getModelResource("currentRefreshId");
//    }
//    
//    public void setCurrentRefreshId(String s) {
//        drmSyncProperties.setProperty("currentRefreshId", s);
//    }
//
//    public String getCatalogName() {
//        return getModelResource("catalogName");
//
//    }
//    public void setCatalogName(String s) {
//          drmSyncProperties.setProperty("catalogName", s);
//
//    }
//    public String getCatalogCode() {
//        return getModelResource("catalogCode");
//    }
//    
//    public void setCatalogCode(String s) {
//        drmSyncProperties.setProperty("catalogCode", s);
//    }
//    
//    public int getBatchSize() {
//        return Integer.parseInt(getModelResource("batchSize"));
//    }
//    public String getOrbitProcessItemURL() {
//        return getModelResource("orbitItemWSURL");
//    }
//    public String getPaasTableName() {
//        return getModelResource("paasTableName");
//    }
//    public String getOrbitProcessCategoryURL() {
//        return getModelResource("orbitCatalogWSURL");
//    }
//    public String getChangedCategoryServiceURL() {
//        return getModelResource("OALSCMDRMSyncServicesGetCategoriesURL");
//    }
//    public String getMaxLevelWSURL() {
//        return getModelResource("getMaxLevelWSURL");
//    }
//    public String getMostRecentRefreshWSURL() {
//        return getModelResource("getMostRecentRefreshWSURL");
//    }
//    
//    public boolean createItemLeaf(String catalog_code) {
//        //case sensitive
//        String val=getModelResource("leafItemCatalog");
//        String[] vals=val.split(",");
//        
//        return Arrays.asList(vals).contains(catalog_code);
//    }
//    public String getDefaultItemClass() {
//        return getModelResource("itemClass");
//    }
//    public String getDefaultItemOrg() {
//        return getModelResource("itemOrg");
//    }
//    public int getLastRefreshId() {
//        return Integer.parseInt(getModelResource("lastRefreshId"));
//    }
//    
//    public String getDefaultItemLCPValue() {
//        return getModelResource("itemLCP");
//    }
//    public String getDefaultItemStatus() {
//        return getModelResource("itemStatus");
//    }
//    public String getDefaultItemUOM() {
//        return getModelResource("itemUom");
//    }
//
//    public String getLoggerFlowName() {
//        return getModelResource("loggerFlowName");
//    }
//    public String getLoggerID() {
//        return getModelResource("loggerID");
//    }
//    
//    
//    
//    public String getDBDriver() {
//        return getModelResource("DB_Driver");
//    }
//    public String getDBURL() {
//        return getModelResource("DB_URL");
//    }
//    public String getDBUser() {
//        return getModelResource("DB_User");
//    }
//    public String getDBPassword() {
//        return getModelResource("DB_Pass");
//    }
//    public String getDBProcedure() {
//        return getModelResource("DB_Proc");
//    }
//    public String getReportFile() {
//        return getModelResource("reportPath")+"/Report.txt";
//    }
//    public String getReportType() {
//        return getModelResource("reportType");
//    }
//   
//    
//    
//    
//    
//}
