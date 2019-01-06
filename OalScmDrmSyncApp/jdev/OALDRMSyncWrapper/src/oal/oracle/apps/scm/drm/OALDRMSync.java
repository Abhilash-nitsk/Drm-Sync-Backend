package oal.oracle.apps.scm.drm;

import com.oracle.apps.scm.drm.exception.DRMCatalogNotFoundException;

import com.oracle.apps.scm.drm.exception.DRMDataSanityTestFailedException;
import com.oracle.apps.scm.drm.exception.DRMPaasServiceException;
import com.oracle.apps.scm.drm.exception.DRMParentDoesnotExistException;

import java.io.FileNotFoundException;
import java.io.FileWriter;

import java.util.Set;

import java.sql.Connection;
import java.sql.DriverManager;

import java.io.IOException;
import java.io.File;

import java.math.BigDecimal;

import java.util.List;

import java.sql.PreparedStatement;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import javax.persistence.EntityManager;

import javax.xml.soap.SOAPException;

import oal.oracle.apps.misegp.drm.OALDRMSyncUtil;

import oal.oracle.apps.scm.drm.orbit.DRMJSONStringUtil;

import oal.oracle.apps.scm.drm.orbit.DRMXMLStringUtil;

import oal.util.logger.*;

import org.json.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

import oal.oracle.apps.scm.drm.orbit.OrbitMicroServiceInvoker;

import org.json.JSONString;

import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.TimeUnit;

import oal.oracle.apps.scm.drm.MailNotification;

import javax.mail.MessagingException;


public class OALDRMSync {

    private String loggerName = OALDRMSync.class.getName();
    private long totaltime;
    private String dateTime;
    private String result = "Process Successful";
    private boolean error_flag = false;
    private Map<String, Set<String>> erroredCategoriesMap = new HashMap();
    // private String catalogCode="";
    // private String catalogName="";

    class Processcat implements Runnable {
        JSONArray olddataArrayC;
        JSONArray dataArrayC;
        int startIndexC;
        int end_indexC;
        int batch_sizeC;
        String catalog_codeC;
        String catalog_nameC;
        ArrayList<Integer> leafIndexC;
        String levelC;

        Processcat(JSONArray olddataArray, JSONArray dataArray, int startIndex, int end_index, int batch_size,
                   String catalog_code, String catalog_name, ArrayList<Integer> leafIndex, String level) {
            olddataArrayC = olddataArray;
            dataArrayC = dataArray;
            startIndexC = startIndex;
            batch_sizeC = batch_size;
            catalog_codeC = catalog_code;
            catalog_nameC = catalog_name;
            end_indexC = end_index;
            levelC = level;
            leafIndexC = leafIndex;

        }

        @Override
        public void run() {
            try {

                process(olddataArrayC, dataArrayC, startIndexC, end_indexC, batch_sizeC, catalog_codeC, catalog_nameC,
                        leafIndexC, levelC);
            } catch (DRMParentDoesnotExistException | IOException | JSONException | SOAPException e) {
            }
        }
    }

    public OALDRMSync() {
        super();
    }

    public static void main(String Args[]) {

        OALDRMSync drmsync = new OALDRMSync();

        try {
            //  drmsync.init("1000", "0BBB", "00", "OAL_TEST_CATEGORY_SYNC");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    public void init(String refreshId, DRMSyncPropertyV2.Catalog catalog) {

        try {

            //  findDelta(refreshId);
            //  loggerName+="["+DRMSyncPropertyV2.getInstance().getCatalogCode(catalog)+"]";
            //

            sync(catalog,refreshId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                String catalog_code = DRMSyncPropertyV2.getInstance().getCatalogCode(catalog);
                String catalog_name = DRMSyncPropertyV2.getInstance().getCatalogName(catalog);
                MailNotification.sendNotification("DRM SYNC REPORT",
                                                  generateReportV2(catalog_code, catalog_name, result),
                                                  generateDetailedReport(catalog_code));
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (DRMPaasServiceException | IOException | JSONException e) {
                e.printStackTrace();
            }
        }

    }

    //@SuppressWarnings("oracle.jdeveloper.java.nested-assignment")
    public void sync(DRMSyncPropertyV2.Catalog catalog,String refresh_id) throws IOException, JSONException, SOAPException,
                                                               DRMCatalogNotFoundException, DRMPaasServiceException,
                                                               DRMParentDoesnotExistException {

        Date date = new Date();
        dateTime = date.toString();
        //System.out.println(dateTime);

        long start = System.currentTimeMillis();
        String catalog_code = DRMSyncPropertyV2.getInstance().getCatalogCode(catalog);
        String catalog_name = DRMSyncPropertyV2.getInstance().getCatalogName(catalog);

        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                          "Initializing DRM Sync.." + "[" + catalog.getValue() + "]");

        if (!catalogExist(catalog_name)) {
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                              DRMSyncPropertyV2.getInstance().getLoggerID(), "fatal",
                              "Unable to find catalog" + "[" + catalog.getValue() + "]");

            throw new DRMCatalogNotFoundException("Unable to find Catalog, Please make sure if catalog exist." + "[" +
                                                  catalog.getValue() + "]");
        }


        JSONArray dataArray;
        JSONArray olddataArray;


        // int mostRecentRefresh = OALDRMSyncUtil.getMostRecentRefresh(DRMSyncProperty.getInstance().getCatalogCode());
        //        int lastRefreshId = DRMSyncProperty.getInstance().getLastRefreshId();
        //
        //        if (mostRecentRefresh == lastRefreshId) {
        //            OalLogger.sendLog(DRMSyncProperty.getInstance().getLoggerFlowName(),loggerName,DRMSyncProperty.getInstance().getLoggerID(),
        //                              "info","All data up to date.");
        //            return;
        //        }

        int currentLevel = 1;
        int maxLevel = OALDRMSyncUtil.getMaxLevel(DRMSyncPropertyV2.getInstance().getCatalogCode(catalog));
        // maxLevel=3;
        // if data exist in DRM at level x, get the data in dataArray

        while (currentLevel <= maxLevel) {

            ExecutorService executorService = Executors.newFixedThreadPool(10);

            System.out.println("current level " + currentLevel);
            String level = String.valueOf(currentLevel);
            //HashSet<String> errored_cat=new HashSet();
            // erroredCategoriesMap.put(level, errored_cat);
            Set<String> erroredCat =
                OALDRMSyncUtil.getErroredCategories(catalog_code, level,
                                                    DRMSyncPropertyV2.getInstance().getCurrentRefreshId());
            erroredCategoriesMap.put(level, erroredCat);

            dataArray = OALDRMSyncUtil.getCategories(catalog_code, currentLevel,refresh_id);
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                              DRMSyncPropertyV2.getInstance().getLoggerID(), "info",
                              "Initializing Category Creation for level " + currentLevel + " [" + catalog.getValue() +
                              "]");


            dataArray = sanityFilter(dataArray, currentLevel);

            if (dataArray.length() == 0) {
                OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                                  DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                                  "No updated record found at level " + level + ", Continuing to next level." + " [" +
                                  catalog.getValue() + "]");
                currentLevel++;
                continue;
            }

            ArrayList<Integer> leafIndex = new ArrayList<>();


            if (DRMSyncPropertyV2.getInstance().createItemLeaf(catalog_code))

                for (int i = 0; i < dataArray.length(); i++) {

                    JSONObject job = dataArray.getJSONObject(i);

                    // adding categories which qualifies for leaf level item creation
                    if (job.getString(DRMJSONStringUtil.CATEGORY_IS_LEAF).equalsIgnoreCase("YES")) {
                        leafIndex.add(i);
                    }
                }

            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                              DRMSyncPropertyV2.getInstance().getLoggerID(), "info",
                              "Total number of nodes at level " + currentLevel + "is " + dataArray.length() + " [" +
                              catalog.getValue() + "]");

            olddataArray = dataArray;
            dataArray = getMaskedData(dataArray, catalog);

            // invoking catalog and item ws with bulk categories and items in batch of batch_size

            int batch_size = DRMSyncPropertyV2.getInstance().getBatchSize();

            int batchNumber = 1;
            int offset = 0;
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                              DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                              "Invoking catalog and item ws with bulk categories and items in batch of " + batch_size +
                              " [" + catalog.getValue() + "]");

            while (dataArray.length() - offset > batch_size) {
                System.out.println("current batch " + batchNumber);

                batchNumber++;
                OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                                  DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                                  "Processing batch " + (batchNumber));
                executorService.execute(new Processcat(olddataArray, dataArray, offset, offset + batch_size, batch_size,
                                                       catalog_code, catalog_name, leafIndex, level));
                //                process(olddataArray, dataArray, offset, offset + batch_size, batch_size,catalog_code, catalog_name, leafIndex,
                //                        level);

                offset = offset + batch_size;
            }
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                              DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                              "Processing last batch of level " + currentLevel + " [" + catalog.getValue() + "]");
            process(olddataArray, dataArray, offset, dataArray.length(), batch_size, catalog_code, catalog_name,
                    leafIndex, level);
            currentLevel++;

            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "info",
                          "DRM Sync Successful !" + " [" + catalog.getValue() + "]");

        //  fw.close();
        // saveRefreshId(mostRecentRefresh);

        long end = System.currentTimeMillis();

        System.out.println("Total time taken: " + (end - start));
        totaltime = end - start;

    }

    @Deprecated
    public String generateReport(String catalogCode, String catalogName) throws DRMPaasServiceException, IOException {

        // FileWriter fw=new FileWriter(DRMSyncProperty.getInstance().getReportFile());
        StringBuilder sb = new StringBuilder();
        sb.append("-----DRM SYNC REPORT SUMMARY-------");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Date Time: " + dateTime);
        sb.append(System.lineSeparator());
        sb.append("CatalogCode: " + catalogCode);
        sb.append(System.lineSeparator());
        sb.append("Catalog Name: " + catalogName);
        sb.append(System.lineSeparator());
        sb.append("Status:" + result);
        sb.append(System.lineSeparator());
        int level = OALDRMSyncUtil.getMaxLevel(catalogCode);
        sb.append("Depth of hierarchy:" + level);
        sb.append(System.lineSeparator());
        sb.append("Processing report");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(String.format("%-10s|%-20s|%-23s|%-20s", "Level", "Processed Categories", "Unprocessed Categories",
                                "Errored Categories"));
        //      sb.append("Level\t|\t");
        //      sb.append("Processed Categories\t|\t");
        //      sb.append("Unprocessed Categories\t|\t");
        //      sb.append("Errored Categories");
        sb.append(System.lineSeparator());
        sb.append("----------------------------------------------------------------------------");
        sb.append(System.lineSeparator());
        for (int i = 1; i <= level; i++) {

            // String.format("|%-20d|%-20d|%-20d|%-20d|", 93);

            // sb.append(i);
            // sb.append("\t|\t");
            int processed = OALDRMSyncUtil.getCount(catalogCode, i, "P");
            // sb.append(processed);
            //sb.append("\t|\t");
            int unprocessed = OALDRMSyncUtil.getCount(catalogCode, i, "U");
            //sb.append(unprocessed);
            //sb.append("\t|\t");
            int errored = OALDRMSyncUtil.getCount(catalogCode, i, "E");

            //sb.append(errored);

            sb.append(String.format("%-10d|%-20d|%-23d|%-20d", i, processed, unprocessed, errored));
            //          sb.append(System.lineSeparator());
            sb.append(System.lineSeparator());
        }
        // fw.write(sb.toString());
        // fw.close();
        return sb.toString();

    }

    public static File generateDetailedReport(String catalogCode) throws IOException, DRMPaasServiceException,
                                                                         JSONException {
        File csvFile = File.createTempFile("Detailed Report", ".csv");


        FileWriter writer = new FileWriter(csvFile);
        int level = OALDRMSyncUtil.getMaxLevel(catalogCode);
        // following one Line should be commented
        // level=3;

        String[] elementHeader = new String[] {
            "Catalog Code", "Category Code", "Category Name", "Category Description", "Level", "Status", "Comments"
        };
        CSVUtils.writeLine(writer, Arrays.asList(elementHeader));
        for (int i = 1; i <= level; i++) {
            JSONArray arr = OALDRMSyncUtil.getCategoriesfromStageTable(catalogCode, i);

            String[] elementNames = new String[] {
                "catalogCode", "categoryCode", "categoryName", "categoryDescription", "levl", "processedFlag",
                "comments"
            };
            for (int j = 0; j < arr.length(); j++) {
                List<String> str = new ArrayList<String>();

                //System.out.printf("%d ELEMENTS IN CURRENT OBJECT:\n", elementNames.length);
                for (String elementName : elementNames) {

                    Object value = arr.getJSONObject(j).opt(elementName);
                    if (value != null) {
                        if (elementName.equals("comments")) {
                            String rawMessage = String.valueOf(value);
                            //  String error=rawMessage.substring(0,rawMessage.indexOf("<MESSAGE>"));
                            int a = rawMessage.indexOf("<TEXT>");
                            int b = rawMessage.indexOf("</TEXT>");
                            String reason = rawMessage.substring(a + 6, b);
                            str.add(reason);
                        } else
                            str.add(String.valueOf(value));
                    } else
                        str.add("");
                    // System.out.printf("name=%s, value=%s\n", elementName, value);
                }
                CSVUtils.writeLine(writer, str);
            }

        }
        writer.close();
        return csvFile;
    }

    public static JSONObject generateReportV2(String catalogCode, String catalogName,
                                              String result) throws DRMPaasServiceException, IOException,
                                                                    JSONException {

        // FileWriter fw=new FileWriter(DRMSyncProperty.getInstance().getReportFile());
        JSONObject jobj = new JSONObject();
        StringBuilder sb = new StringBuilder();
        jobj.put("header", "DRM SYNC REPORT SUMMARY");
        jobj.put("dateTime", new Date().toString());
        jobj.put("catalogCode", catalogCode);
        jobj.put("status", result);
        jobj.put("catalogName", catalogName);

        int level = OALDRMSyncUtil.getMaxLevel(catalogCode);
        // following one Line should be removed
        //level=3;
        jobj.put("maxDepth", String.valueOf(level));
        jobj.put("tableHeader", "Level wise details");

        JSONArray arr = new JSONArray();
        for (int i = 1; i <= level; i++) {
            JSONObject obj = new JSONObject();

            obj.put("level", String.valueOf(i));
            int processed = OALDRMSyncUtil.getCount(catalogCode, i, "P");
            obj.put("processed", String.valueOf(processed));

            int unprocessed = OALDRMSyncUtil.getCount(catalogCode, i, "U");
            obj.put("unprocessed", String.valueOf(unprocessed));

            int errored = OALDRMSyncUtil.getCount(catalogCode, i, "E");
            obj.put("errored", String.valueOf(errored));

            arr.put(obj);
        }

        jobj.put("data", arr);

        return jobj;


    }


    //TO Be uncommented once the PL/SQL Procedure works fine


    private void process(JSONArray olddataArray, JSONArray dataArray, int startIndex, int end_index, int batch_size,
                         String catalog_code, String catalog_name, ArrayList<Integer> leafIndex,
                         String level) throws IOException, JSONException, SOAPException,
                                              DRMParentDoesnotExistException {


        System.out.println("Processing " + (end_index - startIndex) + " no of items for catalog code " + catalog_code +
                           " and level " + level);
        if (!level.equals("1"))
            if (!checkifparentexist(catalog_code, olddataArray, startIndex, end_index))
                throw new DRMParentDoesnotExistException("Parent Doesnot exist for some categories" + " [" +
                                                         catalog_code + "]");


        if (!processCategoriesAndItems(olddataArray, dataArray, startIndex, end_index, batch_size, catalog_code,
                                       catalog_name, leafIndex)) {
            // fw.close();
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                              DRMSyncPropertyV2.getInstance().getLoggerID(), "fatal",
                              "Error while creating category using bulk creation method");
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                              DRMSyncPropertyV2.getInstance().getLoggerID(), "info",
                              "Retrying category creation.." + " [" + catalog_code + "]");
            try {

                mergeCategoryAndItem(olddataArray, dataArray, startIndex, end_index, batch_size, catalog_code,
                                     catalog_name, leafIndex, level);

            } catch (IOException | JSONException | SOAPException e) {
                throw e;
            }
        }

    }


    /**
     * Creates bulk category and leaf level item if requested.
     * @param createItems
     * @param categories
     * @param startIndex
     * @param endIndex
     * @param batch_size
     * @param catalog_code
     * @param leafIndex
     * @return
     */

    private boolean processCategoriesAndItems(JSONArray olddataArray, JSONArray categories, int startIndex,
                                              int endIndex, int batch_size, String catalog_code, String catalog_name,
                                              ArrayList<Integer> leafIndex) {
        JSONArray sublist = new JSONArray();
        for (int m = startIndex; m < endIndex; m++) {
            try {
                sublist.put(categories.getJSONObject(m));
            } catch (JSONException e) {
                OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                                  DRMSyncPropertyV2.getInstance().getLoggerID(), "fatal",
                                  "Failed while creating batches.." + " [" + catalog_code + "]");
                e.printStackTrace();
                return false;
            }
        }
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                          "Invoking catalog Orbit Microserviceservice.." + " [" + catalog_code + "]");
        System.out.println("Invoking catalog Orbit Microserviceservice.." + " [" + catalog_code + "]");
        try {

            if (sublist.length() > 0) {
                OrbitMicroServiceInvoker.invokeCategoryService(sublist, catalog_name);
            }
        } catch (Exception e) {
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                              DRMSyncPropertyV2.getInstance().getLoggerID(), "fatal",
                              "Process failed while creating categories." + " [" + catalog_code + "]");
            e.printStackTrace();
            return false;
        }
        JSONArray itemJSONArray = new JSONArray();

        try {


            for (int j : leafIndex) {
                if (j < startIndex || j >= endIndex)
                    continue;

                JSONObject obj =
                    createItem(categories.getJSONObject(j).getString(DRMJSONStringUtil.CATEGORY_CODE),
                               categories.getJSONObject(j).getString(DRMJSONStringUtil.CATEGORY_DESCRIPTION),
                               categories.getJSONObject(j).getString(DRMJSONStringUtil.CATEGORY_NAME), catalog_code);
                itemJSONArray.put(obj);

            }

            if (itemJSONArray.length() > 0) {
                OrbitMicroServiceInvoker.invokeItemService(itemJSONArray);
            }

        } catch (Exception ex) {
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                              DRMSyncPropertyV2.getInstance().getLoggerID(), "fatal",
                              "Error in Item Creation." + " [" + catalog_code + "]");
            ex.printStackTrace();
            return false;
        }

        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "info",
                          "updating data in stage table" + " [" + catalog_code + "]");
        try {
            updateCategories(catalog_code, olddataArray, startIndex, endIndex, "P");
        } catch (JSONException e) {
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                              DRMSyncPropertyV2.getInstance().getLoggerID(), "fatal",
                              "updating data in stage table failed.." + " [" + catalog_code + "]");
        }
        return true;
    }


    /**
     * Creates single category and leaf level item if requested.
     * @param createItems
     * @param categories
     * @param startIndex
     * @param endIndex
     * @param batch_size
     * @param catalog_code
     * @param leafIndex
     * @return
     */
    private boolean mergeCategoryAndItem(JSONArray olddataArray, JSONArray categories, int startIndex, int endIndex,
                                         int batch_size, String catalogCode, String catalog_name,
                                         ArrayList<Integer> leafIndex, String level) throws JSONException, IOException,
                                                                                            SOAPException {


        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                          "Invoking catalog Orbit Microserviceservice.." + " [" + catalogCode + "]");

        for (int m = startIndex; m < endIndex; m++) {

            String[] out = new String[2];
            try {

                OrbitMicroServiceInvoker.invokeMergeCategoryService(categories.getJSONObject(m), catalog_name, out);
            } catch (IOException | SOAPException e) {
                OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                                  DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                                  "Error while creating :" +
                                  categories.getJSONObject(m).getString(DRMJSONStringUtil.CATEGORY_CODE) + " [" +
                                  catalogCode + "]");
                updateError(catalogCode, olddataArray, m, m + 1, "E");
                OALDRMSyncUtil.updateComments(catalogCode,
                                              olddataArray.getJSONObject(m).getString(DRMJSONStringUtil.CATEGORY_CODE),
                                              out[1]);
                result = "Error encountered during the process";
                erroredCategoriesMap.get(level)
                    .add(olddataArray.getJSONObject(m).getString(DRMJSONStringUtil.CATEGORY_CODE));
                e.printStackTrace();
                if (!error_flag)
                    error_flag = true;
                continue;
                //throw e;
            }
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                              DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                              "Processed :" + categories.getJSONObject(m).get(DRMJSONStringUtil.CATEGORY_CODE) + " [" +
                              catalogCode + "]");

            /// Item Creation
            JSONObject obj = null;
            if (leafIndex.contains(m)) {

                obj =
                    createItem(categories.getJSONObject(m).getString(DRMJSONStringUtil.CATEGORY_CODE),
                               categories.getJSONObject(m).getString(DRMJSONStringUtil.CATEGORY_DESCRIPTION),
                               categories.getJSONObject(m).getString(DRMJSONStringUtil.CATEGORY_NAME), catalog_name);
                try {
                    OrbitMicroServiceInvoker.invokeMergeItemService(obj);
                    OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                                      DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                                      "Processed Item :" +
                                      categories.getJSONObject(m).get(DRMJSONStringUtil.CATEGORY_CODE) + " [" +
                                      catalogCode + "]");
                } catch (IOException | SOAPException e) {
                    OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                                      DRMSyncPropertyV2.getInstance().getLoggerID(), "fatal",
                                      "Error while Item :" +
                                      categories.getJSONObject(m).get(DRMJSONStringUtil.CATEGORY_CODE) + " [" +
                                      catalogCode + "]");
                    throw e;
                }

            }

            try {
                OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                                  DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                                  "updating stage table" + " [" + catalogCode + "]");
                updateCategories(catalogCode, olddataArray, m, m + 1, "P");

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }


        return true;
    }


    private static JSONObject createItem(String itemNumber, String itemDesc, String categoryCode,
                                         String CatalogCode) throws JSONException {
        JSONObject jobj = new JSONObject();
        jobj.put(DRMJSONStringUtil.ITEM_CLASS, DRMSyncPropertyV2.getInstance().getDefaultItemClass());
        jobj.put(DRMJSONStringUtil.ITEM_ORGANIZATION_CODE, DRMSyncPropertyV2.getInstance().getDefaultItemOrg());
        jobj.put(DRMJSONStringUtil.ITEM_NUMBER, itemNumber);
        jobj.put(DRMJSONStringUtil.ITEM_DESCRIPTION, itemDesc);
        jobj.put(DRMJSONStringUtil.ITEM_PRIMARY_UNIT_OF_MEASUREMENT,
                 DRMSyncPropertyV2.getInstance().getDefaultItemUOM());
        jobj.put(DRMJSONStringUtil.ITEM_STATUS_VALUE, DRMSyncPropertyV2.getInstance().getDefaultItemStatus());
        jobj.put(DRMJSONStringUtil.ITEM_LIFECYCLE_PHASE_VALUE,
                 DRMSyncPropertyV2.getInstance().getDefaultItemLCPValue());
        jobj.put(DRMJSONStringUtil.ITEM_CATEGORY, categoryCode);
        jobj.put(DRMJSONStringUtil.ITEM_CATALOG, CatalogCode);

        return jobj;

    }

    private static JSONArray getMaskedData(JSONArray dataArray, DRMSyncPropertyV2.Catalog cat) {

        JSONArray newdata = new JSONArray();

        for (int i = 0; i < dataArray.length(); i++) {
            try {

                JSONObject clone = new JSONObject();
                JSONObject job = dataArray.getJSONObject(i);
                Iterator<?> keys = job.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();

                    if (key.equalsIgnoreCase(DRMJSONStringUtil.CATEGORY_CODE) |
                        key.equalsIgnoreCase(DRMJSONStringUtil.CATEGORY_PARENT_CATEGORY_CODE) |
                        key.equalsIgnoreCase(DRMJSONStringUtil.CATEGORY_NAME))
                        clone.put(key, DRMSyncPropertyV2.getInstance().getPrependString(cat) + job.getString(key));
                    else
                        clone.put(key, job.get(key));
                }
                newdata.put(clone);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return newdata;
    }


    /**
     *
     * Method stores most recent refreshid in property file for future reference.
     */
    @Deprecated
    private void saveRefreshId(int refreshId) {
        //        try {
        //            DRMSyncPropertyV2.getInstance().updateLastRefreshIdToProperty(refreshId);
        //        } catch (FileNotFoundException e) {
        //            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName,
        //                              DRMSyncPropertyV2.getInstance().getLoggerID(),
        //                              "info","Unable to save last refresh id, Please do it manually.");
        //            e.printStackTrace();
        //        } catch (IOException e) {
        //            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName,
        //                              DRMSyncPropertyV2.getInstance().getLoggerID(),
        //                              "info","Unable to save last refresh id, Please do it manually.");
        //            e.printStackTrace();
        //        }
    }

    private boolean catalogExist(String catalog_name) {
        // yet to be implemented
        return true;
    }

    private void updateCategories(String catalogCode, JSONArray olddataArray, int startIndex, int endIndex,
                                  String flag) throws JSONException {


        OALDRMSyncUtil.updateCategoriesV2(catalogCode, olddataArray, startIndex, endIndex, flag);
        //OALDRMSyncUtil.deleteCategories(newarr,startIndex,endIndex);
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "debug",
                          "Successfully updated stage table" + " [" + catalogCode + "]");

    }

    private boolean checkifparentexist(String catalogCode, JSONArray arr, int st, int end) throws JSONException {
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "info",
                          "checking if parents exist" + " [" + catalogCode + "]");
        return OALDRMSyncUtil.parentExist(catalogCode, arr, st, end);
    }

    private void updateError(String catalogCode, JSONArray arr, int a, int b, String flag) throws JSONException {
        OALDRMSyncUtil.updateCategoriesV2(catalogCode, arr, a, b, flag);
    }

    private JSONArray sanityFilter(JSONArray arr, int level) throws JSONException {
        if (level == 1)
            return arr;
        JSONArray array = new JSONArray();
        for (int i = 0; i < arr.length(); i++) {

            String x = arr.getJSONObject(i).getString(DRMJSONStringUtil.CATEGORY_PARENT_CATEGORY_CODE);
            if (erroredCategoriesMap.get(String.valueOf(level - 1)).contains(x)) {
                erroredCategoriesMap.get(String.valueOf(level))
                    .add(arr.getJSONObject(i).getString(DRMJSONStringUtil.CATEGORY_CODE));
            } else {
                array.put(arr.getJSONObject(i));
                //  System.out.print("code"+arr.getJSONObject(i).getString(DRMJSONStringUtil.CATEGORY_CODE)+" ");
            }
        }
        return array;
    }
}
