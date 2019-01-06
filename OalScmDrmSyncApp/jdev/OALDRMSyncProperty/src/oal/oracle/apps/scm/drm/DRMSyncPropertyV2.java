package oal.oracle.apps.scm.drm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

import java.util.Hashtable;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.naming.NamingException;

import javax.sql.DataSource;

import oal.util.logger.OalLogger;


public class DRMSyncPropertyV2 {

    public enum Catalog {
        FPH("fph"),
        APH("aph"),
        CRM("crm");

        Catalog(String v) {
            value = v;
        }
        private String value;

        public String getValue() {
            return value;
        }
    };

    private static final Object lock = new Object();
    private static String loggerName = DRMSyncPropertyV2.class.getName();
    private static DRMSyncPropertyV2 instance = null;
    private Properties drmSyncProperties = new Properties();
    private boolean disableKeystore = true;

    private DRMSyncPropertyV2() {
        super();
    }

    public static DRMSyncPropertyV2 getInstance() {
        if (instance == null)
            try {
                init();
            } catch (NamingException | SQLException e) {
                OalLogger.sendLog("SCM-MDM-DRM-SYNC", loggerName, "SCM-MDM-DRM-SYNC", "fatal",
                                  "Exception occurred while initiating db connection to read property.");

            }
        return instance;
    }


    public static void init() throws SQLException, NamingException {

        if (instance == null) {
            synchronized (lock) {
                instance = new DRMSyncPropertyV2();
                //
                Connection con = null;

                try {
                    con = instance.initDb();
                } catch (NamingException | SQLException e) {
                    OalLogger.sendLog("SCM-MDM-DRM-SYNC", loggerName, "SCM-MDM-DRM-SYNC", "fatal",
                                      "Exception occurred while initiating db connection to read property.");
                    throw e;
                }

                try {
                    instance.readProperties(con);
                } catch (SQLException e) {
                    OalLogger.sendLog("SCM-MDM-DRM-SYNC", loggerName, "SCM-MDM-DRM-SYNC", "fatal",
                                      "Exception occurred while reading property from database.");
                    throw e;
                }

            }
        }

    }

    public static void main(String args[]) throws NamingException, SQLException {
        new DRMSyncPropertyV2().initDb();
    }

    public boolean createItemLeaf(String s) {
        return s.equalsIgnoreCase("XCXCXCXCXCXCXCXCXCXCXCZBBB");
    }

    private Connection initDb() throws NamingException, SQLException {

        Connection connection = null;
        DataSource dataSource = null;
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
        env.put(Context.PROVIDER_URL, "http://localhost:7101"); //Optional
        InitialContext context = new InitialContext(env);
        System.out.println("context.INITIAL_CONTEXT_FACTORY ... " + context.INITIAL_CONTEXT_FACTORY);
        System.out.println(context);
        dataSource = (DataSource) context.lookup("jdbc/OalscmRuntimeERP");
        System.out.println("2  " + dataSource);
        connection = dataSource.getConnection();
        System.out.println("3 " + connection);
        return connection;
    }

    private void readProperties(Connection connection) throws SQLException {

        PreparedStatement ps1 = connection.prepareStatement("select * from OALEGO_DRM_SYNC_PROPERTY");
        ResultSet rs1 = ps1.executeQuery();
        while (rs1.next()) {
            String key = rs1.getString(1);
            String value = rs1.getString(2);
            if (value == null)
                value = "";
            drmSyncProperties.put(key, value);

            OalLogger.sendLog("SCM-MDM-DRM-SYNC", loggerName, "SCM-MDM-DRM-SYNC", "debug",
                              "key:" + key + " value:" + value);

        }


        connection.close();
    }

    private void updateProperties(String key, String value) throws SQLException, NamingException {

        Connection connection = initDb();

        PreparedStatement ps1 =
            connection.prepareStatement("update OALEGO_DRM_SYNC_PROPERTY set value='" + value + "' where key='" + key +
                                        "'");

        ps1.executeUpdate();


        connection.close();
    }
    
    

    public synchronized void updateCurrentRefreshId(String value) throws SQLException, NamingException {

        drmSyncProperties.setProperty("current_refresh_id", value);
        updateProperties("current_refresh_id", value);

    }

    public synchronized void updatelastRefreshId(String value) throws SQLException, NamingException {

        drmSyncProperties.setProperty("last_refresh_id", value);
        updateProperties("last_refresh_id", value);
    }

    public synchronized void updatelastRefreshTimeStamp(String value) throws SQLException, NamingException {

        // drmSyncProperties.setProperty("last_refresh_id", value);
        updateProperties("last_refresh_timestamp", value);
    }


    public String getPrependString(Catalog cat) {
        StringBuilder sb = new StringBuilder();
        sb.append(cat.getValue() + "_prepend_string");
        // sb.append(getCatalogCode(cat));
        //return  drmSyncProperties.getProperty(getCatalogCode());
        return drmSyncProperties.getProperty(sb.toString());
    }
    //    public void setPrependString(Catalog cat,String s) {
    //        StringBuilder sb=new StringBuilder();
    //        sb.append("prepend_string_");
    //        sb.append(getCatalogCode(cat));
    //         drmSyncProperties.setProperty(sb.toString(), s);
    //    }

    public boolean useOrbit() {
        return Integer.parseInt(drmSyncProperties.getProperty("use_orbit")) == 0 ? false : true;
    }

    public String getCurrentRefreshId() {
        return drmSyncProperties.getProperty("current_refresh_id");
    }

    public String getStageTable() {
        return drmSyncProperties.getProperty("stage_table");
    }

    public String getDeltaTable() {
        return drmSyncProperties.getProperty("delta_table");
    }

    public void setCurrentRefreshId(String s) {
        drmSyncProperties.setProperty("current_refresh_id", s);
    }

    public String getCatalogName(Catalog cat) {
        return drmSyncProperties.getProperty(cat.getValue() + "_catalog_name");

    }

    public String getStageTableSeedingProcedure() {

        return drmSyncProperties.getProperty("seed_drm_categories_procedure");

    }

    //    public void setCatalogName(String s) {
    //          drmSyncProperties.setProperty("catalog_name", s);
    //
    //    }
    public String getCatalogCode(Catalog cat) {

        return drmSyncProperties.getProperty(cat.getValue() + "_catalog_code");
    }

    public String getLastRefreshId() {

        return drmSyncProperties.getProperty("last_refresh_id");
    }

    //    public void setCatalogCode(Catalog cat,String code) {
    //
    //        drmSyncProperties.setProperty(cat.getValue()+"_catalog_code", code);
    //    }

    public int getBatchSize() {
        return Integer.parseInt(drmSyncProperties.getProperty("batch_size"));
    }

    public String getCatalogServiceURL() {
        if (useOrbit())
            return getOrbitCatalogServiceURL();
        return getfusionCatalogServiceURL();
    }

    public String getItemServiceURL() {
        if (useOrbit())
            return getOrbitItemServiceURL();
        return getfusionItemServiceURL();
    }


    public String getAuthorizationKey() {
        if (!disableKeystore)
            return getKeyStoreAuthorizationKey();
        if (useOrbit())
            return "dmlrYXMudmkueWFkYXZAb3JhY2xlLmNvbTpWaXlhQDIwMjI=";
        else
            return "b2FsLXBkaC1nZW5lcmljQG9yYWNsZS5jb206V2VsY29tZTEyMw==";

    }

    public String getKeyStoreAuthorizationKey() {

        KeyStoreAccessor.getInstance().getCredentials("OALMDMDRM_MAP", "OALMDMDRM_KEY");
        String username = KeyStoreAccessor.getInstance().getUserName();
        String password = KeyStoreAccessor.getInstance().getPassword();
        String authorization = new sun.misc.BASE64Encoder().encode((username + ":" + password).getBytes());
        return authorization;

    }

    public String getDRMSyncServiceBaseURL() {
        return drmSyncProperties.getProperty("drm_sync_service_base_url");
    }


    public String getfusionItemServiceURL() {
        return drmSyncProperties.getProperty("fusion_item_service_url");
    }

    public String getfusionCatalogServiceURL() {
        return drmSyncProperties.getProperty("fusion_catalog_service_url");
    }

    public String getOrbitCatalogServiceURL() {
        return drmSyncProperties.getProperty("orbit_catalog_service_url");
    }

    public String getOrbitItemServiceURL() {
        return drmSyncProperties.getProperty("orbit_item_service_url");
    }

    public String getPaasTableName() {
        return drmSyncProperties.getProperty("paas_table");
    }

    //    public String getChangedCategoryServiceURL() {
    //        return  drmSyncProperties.getProperty("OALSCMDRMSyncServicesGetCategoriesURL");
    //    }
    //    public String getMaxLevelWSURL() {
    //        return  drmSyncProperties.getProperty("OALSCMDRMSyncServicesGetMaxLevelURL");
    //    }

    public String getDefaultItemClass() {
        return drmSyncProperties.getProperty("default_item_class");
    }

    public String getDefaultItemOrg() {
        return drmSyncProperties.getProperty("default_item_org");
    }

    public String getDefaultItemLCPValue() {
        return drmSyncProperties.getProperty("default_item_lcp");
    }

    public String getDefaultItemStatus() {
        return drmSyncProperties.getProperty("default_item_status");
    }

    public String getDefaultItemUOM() {
        return drmSyncProperties.getProperty("default_item_uom");
    }

    public String getLoggerFlowName() {
        return drmSyncProperties.getProperty("logger_flow_name");
    }

    public String getLoggerID() {
        return drmSyncProperties.getProperty("logger_id");
    }


    public String getDBDriver() {
        return drmSyncProperties.getProperty("db_driver");
    }

    public String getDBURL() {
        return drmSyncProperties.getProperty("db_URL");
    }

    public String getDBUser() {
        return drmSyncProperties.getProperty("db_user");
    }

    public String getDBPassword() {
        return drmSyncProperties.getProperty("db_pass");
    }

    public String getDBProcedure() {
        return drmSyncProperties.getProperty("db_procedure");
    }

}
