package oal.oracle.apps.misegp.drm;

import oal.oracle.apps.scm.drm.DRMSyncPropertyV2;

import com.oracle.apps.scm.drm.exception.DRMPaasServiceException;

import java.util.ArrayList;
import java.util.List;
import oal.util.logger.*;
import java.util.Set;
import java.util.HashSet;
import javax.ws.rs.client.Client;

import javax.ws.rs.client.ClientBuilder;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.ws.rs.core.UriBuilder;

//import oal.oracle.apps.misegp.drm.entities.OalegoDrmSyncData;

import org.json.JSONArray;
import org.json.JSONObject;

import org.json.JSONException;

public class OALDRMSyncUtil {
    private static String loggerName=OALDRMSyncUtil.class.getName();
    private static String currentRefreshId="";
    public OALDRMSyncUtil() {
        super();
    }
        public static void main(String args[]){
//            JSONArray res = getCategories();
//            System.out.println("The response of the API call is : ");
//            System.out.println("The Json Array is : "+res);
//            SaasWsCall(res);
      }
        
        private static void SaasWsCall(JSONArray input){
            JSONArray levelJSON = new JSONArray();
            System.out.println("The database reading complete");
            String level = "2";
            try {
                for(int i =0; i<input.length(); i++){
                    JSONObject job = new JSONObject();
                    job = input.getJSONObject(i);
                if(job.getString("levl").equals(level)){
                        levelJSON.put(job);
                    }
                }
                System.out.println("The list is : ");
                System.out.println(levelJSON);
               // ItemCatalogServiceSoapHttpPortClient.invokeCatalogWebServiceProcess(levelJSON);
               // ItemCatalogServiceSoapHttpPortClient.invok
            }
            catch(Exception e){
                System.out.println("--------------------- ***\nThe Exception Message   :   "+e.getMessage());
            }
        }
        
        //------------------------------------------------------------------------------------------  
        @Deprecated
        public static int getMostRecentRefresh(String catalogCode) throws DRMPaasServiceException {
            return 1;
            /*
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                          DRMSyncPropertyV2.getInstance().getLoggerID(),
                              "debug","Calling paas ws to get most recent refreshId");
            int offset = 0;
            int serviceLimit = 400;
            String url = DRMSyncPropertyV2.getInstance().getMostRecentRefreshWSURL();
            String itemsUrlWithParams = UriBuilder.fromUri(url)
                                                             
                                                              .queryParam("catalogCode", catalogCode)
                                                              
                                                              .toString();
                
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(itemsUrlWithParams);
                Response clientResponse = target.request().get();
                
                String itemsJson=null;
                if (clientResponse.getStatus() != 200) {
                    
                    OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                              DRMSyncPropertyV2.getInstance().getLoggerID(),
                                      "fatal","Service Failed with response code: "+clientResponse.getStatus());
                    System.out.println("Service Failed");
                    throw new DRMPaasServiceException("Return Code"+clientResponse.getStatus());
                    
                } else {
                    itemsJson = clientResponse.readEntity(String.class);
                   
                    }
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                          DRMSyncPropertyV2.getInstance().getLoggerID(),
                              "debug","Most recent refreshId :"+itemsJson);
                return Integer.parseInt(itemsJson);
                */
        }
        
        public static int getCount(String catalogCode, int level,String processedFlag) throws DRMPaasServiceException {
            
            String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
                         url+="/OALSCMDRMSyncServices/service/category/getCategoryCount";
           
                String itemsUrlWithParams = UriBuilder.fromUri(url)
                                                             
                                                              .queryParam("catalogCode", catalogCode)
            .queryParam("refreshId", DRMSyncPropertyV2.getInstance().getCurrentRefreshId())
            .queryParam("levl",level)
            .queryParam("processedFlag",processedFlag)
                                                              .toString();
                
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(itemsUrlWithParams);
                Response clientResponse = target.request().get();
                
                String itemsJson=null;
                if (clientResponse.getStatus() != 200) {
                    OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                              DRMSyncPropertyV2.getInstance().getLoggerID(),
                                      "fatal","Service Failed with response code: "+clientResponse.getStatus());
                    System.out.println("Service Failed");
                    throw new DRMPaasServiceException("Return Code"+clientResponse.getStatus());
                    
                } else {
                    itemsJson = clientResponse.readEntity(String.class);
                   
                    }
                   
                return Integer.parseInt(itemsJson);
                
        }
        
        public static int getMaxLevel(String catalogCode) throws DRMPaasServiceException {
            
            
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                          DRMSyncPropertyV2.getInstance().getLoggerID(),
                              "debug","Calling paas ws to get max level of catalog : "+catalogCode);
            int offset = 0;
            int serviceLimit = 400;
            String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
            url+="/OALSCMDRMSyncServices/service/category/getMaxLevel";
           
                String itemsUrlWithParams = UriBuilder.fromUri(url)
                                                             
                                                              .queryParam("catalogCode", catalogCode)
                                                              
                                                              .toString();
                
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(itemsUrlWithParams);
                Response clientResponse = target.request().get();
                
                String itemsJson=null;
                if (clientResponse.getStatus() != 200) {
                    OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                              DRMSyncPropertyV2.getInstance().getLoggerID(),
                                      "fatal","Service Failed with response code: "+clientResponse.getStatus());
                    System.out.println("Service Failed");
                    throw new DRMPaasServiceException("Return Code"+clientResponse.getStatus());
                    
                } else {
                    itemsJson = clientResponse.readEntity(String.class);
                   
                    }
                   
                return Integer.parseInt(itemsJson);
                
        }
        
        public static Set<String> getErroredCategories(String catalogCode, String level,String refreshId) throws DRMPaasServiceException, JSONException {
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                          DRMSyncPropertyV2.getInstance().getLoggerID(),
                              "debug","Calling paas ws to get errored categories for catalog : "+catalogCode +", level :"+level);
            
            Set<String> CatList = new HashSet<String>(); 
            JSONArray retArr =new JSONArray();
            
                int offset = 0;
                int serviceLimit = 400;
                //String url = DRMSyncPropertyV2.getInstance().getChangedCategoryServiceURL();
            String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
                           url+=  "/OALSCMDRMSyncServices/service/category/getCategoryCatalog";
                //String url = "https://129.144.59.9/OalScmCategorySync/service/category/getCategories";
                int sl,os,count = 0;
                JSONArray jsonArray = null;
                do{
                    
                   UriBuilder builder  = UriBuilder.fromUri(url)
                                                                  .queryParam("offset", offset)
                                                                  .queryParam("limit", serviceLimit)
                                                                  .queryParam("catalogCode", catalogCode)
                                                                  .queryParam("levl", level)
                    .queryParam("refreshId", level)
                    .queryParam("processedFlag", "E");
                                                                  //.queryParam("refreshId", refreshId);
        //                                                                  .toString();
        //                    if(refreshId!=-1) {
        //                        builder=builder.queryParam("refreshId", refreshId);
        //                    }
                    
                    String itemsUrlWithParams = builder.toString();
                    Client client = ClientBuilder.newClient();
                    WebTarget target = client.target(itemsUrlWithParams);
                    Response clientResponse = target.request().get();
                    
                    String itemsJson=null;
                    if (clientResponse.getStatus() != 200) {
                        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                                  DRMSyncPropertyV2.getInstance().getLoggerID(),
                                          "fatal","Service Failed with response code: "+clientResponse.getStatus());
                        System.out.println("Service Failed");
                        throw new DRMPaasServiceException("Return Code"+clientResponse.getStatus());
                        
                    } else {
                        itemsJson = clientResponse.readEntity(String.class);
                        jsonArray = new JSONArray(itemsJson);
//                        for(int i=0;i<jsonArray.length();i++){
//                            JSONObject job = new JSONObject();
//                            job = jsonArray.getJSONObject(i);
//                            retArr.put(job);
//                        }
                        for (int i = 0; i < jsonArray.length(); i++)
                            CatList.add(jsonArray.getJSONObject(i).getString("categoryCode"));
                        //System.out.println(CatList.size());
                    }
                    //System.out.println(CatList);
                    
                   
                    offset+=jsonArray.length();
                }
                while(jsonArray.length()==serviceLimit);
                
                System.out.println(CatList.size());
                
           
            //System.out.println("The Array is : \n"+retArr.length());
            return CatList;
        }
        
        public static JSONArray getCategories(String catalogCode, int level,String refresh_id) throws DRMPaasServiceException, JSONException {
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                          DRMSyncPropertyV2.getInstance().getLoggerID(),
                              "debug","Calling paas ws to get categories for catalog : "+catalogCode +", level :"+level);
            
            List<String> CatList = new ArrayList<String>(); 
            JSONArray retArr =new JSONArray();
            
                int offset = 0;
                int serviceLimit = 400;
                String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
                url+="/OALSCMDRMSyncServices/service/categoryChange/getChangedCategories";
                //String url = "http://localhost:7101/OALSCMDRMSyncServices/service/category/getCategoryCatalog";
                //String url = "https://129.144.59.9/OalScmCategorySync/service/category/getCategories";
                int sl,os,count = 0;
                JSONArray jsonArray = null;
                do{
                    
                   UriBuilder builder  = UriBuilder.fromUri(url)
                                                                  .queryParam("offset", offset)
                                                                  .queryParam("limit", serviceLimit)
                                                                  .queryParam("catalogCode", catalogCode)
                                                                  .queryParam("levl", level)
                    .queryParam("refresh_id", refresh_id);
                                                                  //.queryParam("refreshId", refreshId);
//                                                                  .toString();
//                    if(refreshId!=-1) {
//                        builder=builder.queryParam("refreshId", refreshId);
//                    }
                    
                    String itemsUrlWithParams = builder.toString();
                    Client client = ClientBuilder.newClient();
                    WebTarget target = client.target(itemsUrlWithParams);
                    Response clientResponse = target.request().get();
                    
                    String itemsJson=null;
                    if (clientResponse.getStatus() != 200) {
                        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                                  DRMSyncPropertyV2.getInstance().getLoggerID(),
                                          "fatal","Service Failed with response code: "+clientResponse.getStatus());
                        System.out.println("Service Failed");
                        throw new DRMPaasServiceException("Return Code"+clientResponse.getStatus());
                        
                    } else {
                        itemsJson = clientResponse.readEntity(String.class);
                        jsonArray = new JSONArray(itemsJson);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject job = new JSONObject();
                            job = jsonArray.getJSONObject(i);
                            retArr.put(job);
                        }
                        for (int i = 0; i < jsonArray.length(); i++)
                            CatList.add(jsonArray.get(i).toString());
                        //System.out.println(CatList.size());
                    }
                    //System.out.println(CatList);
                    
                   
                    offset+=jsonArray.length();
                }
                while(jsonArray.length()==serviceLimit);
                
                System.out.println(CatList.size());
                
           
            //System.out.println("The Array is : \n"+retArr.length());
            return retArr;
        }
        
        public static JSONArray getCategories(String catalogCode, int level,int offset,int serviceLimit) throws DRMPaasServiceException, JSONException {
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                          DRMSyncPropertyV2.getInstance().getLoggerID(),
                              "debug","Calling paas ws to get categories for catalog : "+catalogCode +", level :"+level);
            
            List<String> CatList = new ArrayList<String>(); 
            JSONArray retArr =new JSONArray();
            
              
                String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
                url+="/OALSCMDRMSyncServices/service/categoryChange/getChangedCategories";
                //String url = "http://localhost:7101/OALSCMDRMSyncServices/service/category/getCategoryCatalog";
                //String url = "https://129.144.59.9/OalScmCategorySync/service/category/getCategories";
                int sl,os,count = 0;
                JSONArray jsonArray = null;
                UriBuilder builder  = UriBuilder.fromUri(url)
                                                                  .queryParam("offset", offset)
                                                                  .queryParam("limit", serviceLimit)
                                                                  .queryParam("catalogCode", catalogCode)
                                                                  .queryParam("levl", level);
                                                                  //.queryParam("refreshId", refreshId);
        //                                                                  .toString();
        //                    if(refreshId!=-1) {
        //                        builder=builder.queryParam("refreshId", refreshId);
        //                    }
                    
                    String itemsUrlWithParams = builder.toString();
                    Client client = ClientBuilder.newClient();
                    WebTarget target = client.target(itemsUrlWithParams);
                    Response clientResponse = target.request().get();
                    
                    String itemsJson=null;
                    if (clientResponse.getStatus() != 200) {
                        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                                  DRMSyncPropertyV2.getInstance().getLoggerID(),
                                          "fatal","Service Failed with response code: "+clientResponse.getStatus());
                        System.out.println("Service Failed");
                        throw new DRMPaasServiceException("Return Code"+clientResponse.getStatus());
                        
                    } else {
                        itemsJson = clientResponse.readEntity(String.class);
                        jsonArray = new JSONArray(itemsJson);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject job = new JSONObject();
                            job = jsonArray.getJSONObject(i);
                            retArr.put(job);
                        }
                        for (int i = 0; i < jsonArray.length(); i++)
                            CatList.add(jsonArray.get(i).toString());
                        //System.out.println(CatList.size());
                    }
                    //System.out.println(CatList);
                    
                   
                    offset+=jsonArray.length();
                
               
                
                System.out.println(CatList.size());
                
           
            //System.out.println("The Array is : \n"+retArr.length());
            return retArr;
        }
        
        
        public static JSONArray getCategoriesfromStageTable(String catalogCode, int level) throws DRMPaasServiceException, JSONException {
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                          DRMSyncPropertyV2.getInstance().getLoggerID(),
                              "debug","Calling paas ws to get categories for catalog : "+catalogCode +", level :"+level);
            
            List<String> CatList = new ArrayList<String>(); 
            JSONArray retArr =new JSONArray();
            
                int offset = 0;
                int serviceLimit = 400;
                String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
                url+="/OALSCMDRMSyncServices/service/category/getCategoryCatalog";
                //String url = "http://localhost:7101/OALSCMDRMSyncServices/service/category/getCategoryCatalog";
                //String url = "https://129.144.59.9/OalScmCategorySync/service/category/getCategories";
                int sl,os,count = 0;
                JSONArray jsonArray = null;
                do{
                    
                   UriBuilder builder  = UriBuilder.fromUri(url)
                                                                  .queryParam("offset", offset)
                                                                  .queryParam("limit", serviceLimit)
                                                                  .queryParam("catalogCode", catalogCode)
                                                                  .queryParam("levl", level);
                                                                  //.queryParam("refreshId", refreshId);
        //                                                                  .toString();
        //                    if(refreshId!=-1) {
        //                        builder=builder.queryParam("refreshId", refreshId);
        //                    }
                    
                    String itemsUrlWithParams = builder.toString();
                    Client client = ClientBuilder.newClient();
                    WebTarget target = client.target(itemsUrlWithParams);
                    Response clientResponse = target.request().get();
                    
                    String itemsJson=null;
                    if (clientResponse.getStatus() != 200) {
                        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                                  DRMSyncPropertyV2.getInstance().getLoggerID(),
                                          "fatal","Service Failed with response code: "+clientResponse.getStatus());
                        System.out.println("Service Failed");
                        throw new DRMPaasServiceException("Return Code"+clientResponse.getStatus());
                        
                    } else {
                        itemsJson = clientResponse.readEntity(String.class);
                        jsonArray = new JSONArray(itemsJson);
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject job = new JSONObject();
                            job = jsonArray.getJSONObject(i);
                            retArr.put(job);
                        }
                        for (int i = 0; i < jsonArray.length(); i++)
                            CatList.add(jsonArray.get(i).toString());
                        //System.out.println(CatList.size());
                    }
                    //System.out.println(CatList);
                    
                   
                    offset+=jsonArray.length();
                }
                while(jsonArray.length()==serviceLimit);
                
                System.out.println(CatList.size());
                
           
            //System.out.println("The Array is : \n"+retArr.length());
            return retArr;
        }
        
        @Deprecated
        public static String getCurrentRefreshIdV1(String catalogCode) throws DRMPaasServiceException {
            
            if(!currentRefreshId.isEmpty())
                return currentRefreshId;
                    
            String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
                             url+="/OALSCMDRMSyncServices/service/categoryChange/getCurrentRefreshId";
                //String url = "http://localhost:7101/OALSCMDRMSyncServices/service/category/getCategoryCatalog";
                //String url = "https://129.144.59.9/OalScmCategorySync/service/category/getCategories";
                int sl,os,count = 0;
                JSONArray jsonArray = null;
               
                    
                   UriBuilder builder  = UriBuilder.fromUri(url);
                    String itemsUrlWithParams = builder.toString();
                    Client client = ClientBuilder.newClient();
                    WebTarget target = client.target(itemsUrlWithParams);
                    Response clientResponse = target.request().get();
                    
                    String itemsJson=null;
                      
                        if (clientResponse.getStatus() != 200) {
                            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                              DRMSyncPropertyV2.getInstance().getLoggerID(),
                                              "fatal","Service Failed with response code: "+clientResponse.getStatus());
                            System.out.println("Service Failed");
                            throw new DRMPaasServiceException("Return Code"+clientResponse.getStatus());
                            
                        } else {
                            itemsJson = clientResponse.readEntity(String.class);
                           
                            }
                    currentRefreshId=itemsJson;    
            return itemsJson;
        }
        
        
        //--------------------------------------------------------------------------------------------------------------
//        public static String postCategories(OalegoDrmSyncData[] arr){
////        
//            String clientResponse=null;
//            try{
//                String url = "http://localhost:7101/OalScmCategorySync/service/category/postCategories";
//                Client client = ClientBuilder.newClient();
//                WebTarget target = client.target(url);
//                clientResponse = target.request().post(Entity.entity(arr,"application/json"), String.class);
//                System.out.println(clientResponse);
//            }
//            catch(Exception e){
//                e.printStackTrace();
//            }
//            return clientResponse;
//        }
        
        public static String updateCategories(JSONArray arr,String catalogCode){
        //
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                          DRMSyncPropertyV2.getInstance().getLoggerID(),
                          "debug","calling paas ws to Update processed flag in stage table");
        
            String clientResponse=null;
            try{
                String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
                           url+=  "/OALSCMDRMSyncServices/service/category/updateProcessed";
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(url);
                clientResponse = target.request(MediaType.TEXT_PLAIN).accept(MediaType.APPLICATION_JSON).put(Entity.json(arr.toString()), String.class);
                System.out.println(clientResponse);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return clientResponse;
        }
        
        public static String updateCategoriesV2(String catalogCode,JSONArray arr,int a,int b,String processed_flag) throws JSONException {
        //
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                          DRMSyncPropertyV2.getInstance().getLoggerID(),
                          "debug","calling paas ws to Update processed flag in stage table");
        
        StringBuilder sb=new StringBuilder();
            for(int i=a;i<arr.length()&& i<b ;i++) {
                JSONObject jobj=arr.getJSONObject(i);
             sb.append("'");
             sb.append(jobj.getString("categoryCode"));
             sb.append("'");
             sb.append(",");
            }
        
        sb.setLength(sb.length()-1);
            String clientResponse=null;
            try{
                String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
                            url+= "/OALSCMDRMSyncServices/service/category/updateProcessedFlag";
                
                UriBuilder builder  = UriBuilder.fromUri(url).queryParam("catalogCode", catalogCode)
                                                               .queryParam("refreshId", DRMSyncPropertyV2.getInstance().getCurrentRefreshId())
                .queryParam("processedFlag", processed_flag);
                
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(builder.toString());
                clientResponse = target.request(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).put(Entity.text(sb.toString()), String.class);
                System.out.println("updated processed flag for "+sb.toString()+" with response "+clientResponse+" catalog code "+catalogCode);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return clientResponse;
        }
        
        
        public static String updateComments(String catalogCode,String categoryCode, String comments) throws JSONException {
        //
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                          DRMSyncPropertyV2.getInstance().getLoggerID(),
                          "debug","calling paas ws to Update comments in stage table");
        
       
        
       
            String clientResponse=null;
            try{
                String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
                            url+= "/OALSCMDRMSyncServices/service/category/updateComments";
                
                UriBuilder builder  = UriBuilder.fromUri(url).queryParam("catalogCode", catalogCode)
                                                               .queryParam("refreshId", DRMSyncPropertyV2.getInstance().getCurrentRefreshId())
                .queryParam("categoryCode", categoryCode);
                
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(builder.toString());
                clientResponse = target.request(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).put(Entity.text(comments), String.class);
                System.out.println(clientResponse);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return clientResponse;
        }
        
        
        public static boolean parentExist(String catalogCode,JSONArray arr,int a,int b) throws JSONException {
        //
//        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
//                          DRMSyncPropertyV2.getInstance().getLoggerID(),
//                          "debug","calling paas ws to check if parent exist");
        Set<String> catcode=new HashSet();
        StringBuilder sb=new StringBuilder();
            for(int i=a;i<arr.length()&& i<b ;i++) {
                
             catcode.add(arr.getJSONObject(i).getString("parentCategoryCode"));
             
            }
            for(String s:catcode) {
                sb.append("'");
                sb.append(s);
                sb.append("'");
                sb.append(",");
            }
        
        sb.setLength(sb.length()-1);
        
            OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                              DRMSyncPropertyV2.getInstance().getLoggerID(),
                              "debug","calling paas ws to check if parent exist for catalog "+catalogCode+" and categories "+sb.toString());
            String clientResponse=null;
            try{
                String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
                             url+="/OALSCMDRMSyncServices/service/category/getNoOfProcessedCatgories";
                
                UriBuilder builder  = UriBuilder.fromUri(url).queryParam("catalogCode", catalogCode)
                                                               .queryParam("refreshId",
                                                       DRMSyncPropertyV2.getInstance().getCurrentRefreshId());
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(builder.toString());
                clientResponse = target.request(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).post(Entity.text(sb.toString()), String.class);
                System.out.println(clientResponse);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            if(Integer.parseInt(clientResponse)==catcode.size())
                return true;
            System.out.println("Parent does not exist for some of categories among "+sb.toString());
            return false;
        }
        
        
        
        public static String deleteCategories(String catalogCode,JSONArray arr,int a,int b) throws JSONException {
        //
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName+"["+catalogCode+"]",
                          DRMSyncPropertyV2.getInstance().getLoggerID(),
                          "debug","delete in stage table");
        
        StringBuilder sb=new StringBuilder();
            for(int i=a;i<arr.length()&& i<b ;i++) {
                JSONObject jobj=arr.getJSONObject(i);
             sb.append("'");
             sb.append(jobj.getString("categoryCode"));
             sb.append("'");
             sb.append(",");
            }
        
        sb.setLength(sb.length()-1);
            String clientResponse=null;
            try{
                String url = DRMSyncPropertyV2.getInstance().getDRMSyncServiceBaseURL();
                             url+="/OALSCMDRMSyncServices/service/categoryChange/deleteProcessed";
                UriBuilder builder  = UriBuilder.fromUri(url).queryParam("catalogCode",catalogCode)
                                                               .queryParam("refreshId",
                                                       DRMSyncPropertyV2.getInstance().getCurrentRefreshId());
                
                Client client = ClientBuilder.newClient();
                WebTarget target = client.target(builder.toString());
                clientResponse = target.request(MediaType.TEXT_PLAIN).accept(MediaType.TEXT_PLAIN).put(Entity.text(sb.toString()), String.class);
                System.out.println(clientResponse);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return clientResponse;
        }
        
        
        
    }
