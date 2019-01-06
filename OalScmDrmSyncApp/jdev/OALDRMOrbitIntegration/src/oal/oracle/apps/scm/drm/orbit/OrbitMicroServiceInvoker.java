package oal.oracle.apps.scm.drm.orbit;

import oal.oracle.apps.scm.drm.DRMSyncPropertyV2;
import org.apache.commons.lang.StringEscapeUtils;
import java.io.ByteArrayOutputStream;

import java.io.IOException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import java.io.StringReader;
import oal.util.logger.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import java.text.SimpleDateFormat;

import java.util.Base64;
import java.util.Date;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import oal.oracle.apps.scm.drm.DRMSyncPropertyV2;
import oal.oracle.apps.scm.drm.orbit.KeyStoreAccessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class OrbitMicroServiceInvoker {
    
    private static String loggerName = OrbitMicroServiceInvoker.class.getName();
    public OrbitMicroServiceInvoker() {
        super();
    }
    
    
    
    public static boolean invokeMergeItemService(JSONObject dataArray) throws SOAPException, IOException {
         //yet to be implemented
         return true;      
    }
    
    public static boolean invokeItemService(JSONArray dataArray) throws SOAPException, IOException, JSONException {
        
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName,DRMSyncPropertyV2.getInstance().getLoggerID(),
                          "debug","Invoking MergeItem through Orbit Microservice");
        //Create soap message for the web service request    
        MessageFactory messageFactory =  MessageFactory.newInstance();
        //SOAPConstants.SOAP_1_2_PROTOCOL
            //MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        //KeyStoreAccessor keyStoreAccessor=new KeyStoreAccessor();
        //LookUpDAO lookUpDAO=new LookUpDAO();
        
        //Add Namespace Declaration
        soapEnvelope.addNamespaceDeclaration("soapenv","http://schemas.xmlsoap.org/soap/envelope/");
        soapEnvelope.addNamespaceDeclaration("mod","http://xmlns.oracle.com/apps/flex/fnd/applcore/attachments/model/");
        soapEnvelope.addNamespaceDeclaration("item3","http://xmlns.oracle.com/apps/scm/productModel/items/flex/itemGdf/");
        soapEnvelope.addNamespaceDeclaration("item2","http://xmlns.oracle.com/apps/scm/productModel/items/flex/item/");
        soapEnvelope.addNamespaceDeclaration("cat2","http://xmlns.oracle.com/apps/scm/productCatalogManagement/advancedItems/flex/egoItemEff/item/categories/");
        soapEnvelope.addNamespaceDeclaration("cat1","http://xmlns.oracle.com/apps/scm/productCatalogManagement/advancedItems/flex/egoItemEff/itemRevision/categories/");
        soapEnvelope.addNamespaceDeclaration("item1","http://xmlns.oracle.com/apps/scm/productModel/items/flex/itemRevision/");
        soapEnvelope.addNamespaceDeclaration("cat","http://xmlns.oracle.com/apps/scm/productCatalogManagement/advancedItems/flex/egoItemEff/itemSupplier/categories/");
        soapEnvelope.addNamespaceDeclaration("item","http://xmlns.oracle.com/apps/scm/productModel/items/itemServiceV2/");
        soapEnvelope.addNamespaceDeclaration("typ","http://xmlns.oracle.com/apps/scm/productModel/items/itemServiceV2/types/");
        // soapEnvelope.addNamespaceDeclaration("soapenv","http://schemas.xmlsoap.org/soap/envelope/");
        
        //Authentication of soap request in header part
        //   SOAPHeader header = soapEnvelope.getHeader();
        
            String authorization = DRMSyncPropertyV2.getInstance().getAuthorizationKey();
           
        MimeHeaders hd = soapMessage.getMimeHeaders();
        // hd.setHeader("Content-Type","application/soap+xml;charset=UTF-8;");
        hd.addHeader("Authorization","Basic "+authorization);
     
        
        //Populate the body part of the soap request
            SOAPBody soapBody = soapEnvelope.getBody();
        
         //   QName bodyName = new QName("processCategory", "typ");
            SOAPElement processItem = soapBody.addChildElement(DRMXMLStringUtil.ITEM_OPERATION_PROCESS_ITEM,"typ");
            
            
            
            SOAPElement changeOperation = processItem.addChildElement(DRMXMLStringUtil.ITEM_CHANGE_OPERATION,"typ");
            changeOperation.addTextNode("Merge");

          //  SOAPElement category = processCategory.addChildElement("category","typ");
        //            documentNumber.addTextNode("100");

            
            for(int i=0;i<dataArray.length();i++)
            {
                
                JSONObject jobj=dataArray.getJSONObject(i);
                
                SOAPElement item = processItem.addChildElement(DRMXMLStringUtil.ITEM,"typ");
                
                SOAPElement org_code = item.addChildElement(DRMXMLStringUtil.ITEM_ORGANIZATION_CODE,DRMXMLStringUtil.ITEM);
                org_code.addTextNode(jobj.getString(DRMJSONStringUtil.ITEM_ORGANIZATION_CODE));
                
                SOAPElement item_class = item.addChildElement(DRMXMLStringUtil.ITEM_CLASS,DRMXMLStringUtil.ITEM);
                item_class.addTextNode(jobj.getString(DRMJSONStringUtil.ITEM_CLASS));
                    
                SOAPElement item_number = item.addChildElement(DRMXMLStringUtil.ITEM_NUMBER,DRMXMLStringUtil.ITEM);
                item_number.addTextNode(jobj.getString(DRMJSONStringUtil.ITEM_NUMBER));
                
                SOAPElement item_desc = item.addChildElement(DRMXMLStringUtil.ITEM_DESCRIPTION,DRMXMLStringUtil.ITEM);
                item_desc.addTextNode(jobj.getString(DRMJSONStringUtil.ITEM_DESCRIPTION)); 
                
                SOAPElement item_status = item.addChildElement(DRMXMLStringUtil.ITEM_STATUS_VALUE,DRMXMLStringUtil.ITEM);
                item_status.addTextNode(jobj.getString(DRMJSONStringUtil.ITEM_STATUS_VALUE));
                
                SOAPElement lcp = item.addChildElement(DRMXMLStringUtil.LIFECYCLE_PHASE_VALUE,DRMXMLStringUtil.ITEM);
                lcp.addTextNode(jobj.getString(DRMJSONStringUtil.ITEM_LIFECYCLE_PHASE_VALUE));
                
                SOAPElement uom = item.addChildElement(DRMXMLStringUtil.PRIMARY_UNIT_OF_MEASUREMENT,DRMXMLStringUtil.ITEM);
                uom.addTextNode(jobj.getString(DRMJSONStringUtil.ITEM_PRIMARY_UNIT_OF_MEASUREMENT));
                
                    
                SOAPElement stdate = item.addChildElement(DRMXMLStringUtil.START_DATE,DRMXMLStringUtil.ITEM);
                Date date = new Date();
                String modifiedDate= new SimpleDateFormat(DRMXMLStringUtil.DATE_FORMAT).format(date);
                
                stdate.addTextNode(modifiedDate);
                
                SOAPElement item_category = item.addChildElement(DRMXMLStringUtil.ITEM_CATEGORY,DRMXMLStringUtil.ITEM);
                
                SOAPElement item_catalog = item_category.addChildElement(DRMXMLStringUtil.ITEM_CATALOG,DRMXMLStringUtil.ITEM);
                item_catalog.addTextNode(jobj.getString(DRMJSONStringUtil.ITEM_CATALOG));
                
                SOAPElement cat_name = item_category.addChildElement(DRMXMLStringUtil.CATEGORY_NAME,DRMXMLStringUtil.ITEM);
                cat_name.addTextNode(jobj.getString(DRMJSONStringUtil.ITEM_CATEGORY));
                
                              
            } 
            
            soapMessage.saveChanges();
            
            System.out.println(soapMessage.toString());        
            
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            URL endpoint = new URL(DRMSyncPropertyV2.getInstance().getItemServiceURL());

            //Throw Exception if the recipe call times out
            
            //Recipe call
            SOAPMessage response = soapConnection.call(soapMessage, endpoint);
        
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapMessage.writeTo(out);
            String strMsg = new String(out.toByteArray());
            System.out.print(strMsg);
            //Parse the recipe response
            SOAPBody responseSoapBody = response.getSOAPBody();
            java.util.Iterator iterator = responseSoapBody.getChildElements();
            SOAPBodyElement bodyElement2 = (SOAPBodyElement)iterator.next();
        if(bodyElement2.getTagName().contains("Fault"))
            throw new SOAPException();
            String responseBody = bodyElement2.toString();
          //  System.out.print(responseBody);
        
        return true;
                
       
    }
    
    public static boolean invokeCategoryService(JSONArray dataArray,String CatalogName) throws SOAPException,
                                                                                                 IOException,
                                                                                                JSONException {
        
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName,DRMSyncPropertyV2.getInstance().getLoggerID(),
                          "debug","Invoking ProcessCategory through Orbit Microservice");
        //Create soap message for the web service request    
        MessageFactory messageFactory =  MessageFactory.newInstance();
        //SOAPConstants.SOAP_1_2_PROTOCOL
            //MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        //KeyStoreAccessor keyStoreAccessor=new KeyStoreAccessor();
        //LookUpDAO lookUpDAO=new LookUpDAO();
        
        //Add Namespace Declaration
        soapEnvelope.addNamespaceDeclaration("soapenv","http://schemas.xmlsoap.org/soap/envelope/");
        soapEnvelope.addNamespaceDeclaration("typ","http://xmlns.oracle.com/apps/scm/productModel/catalogs/itemCatalogService/types/");
        soapEnvelope.addNamespaceDeclaration("item","http://xmlns.oracle.com/apps/scm/productModel/catalogs/itemCatalogService/");
        soapEnvelope.addNamespaceDeclaration("cat","http://xmlns.oracle.com/apps/scm/productModel/catalogs/flex/category/");
        soapEnvelope.addNamespaceDeclaration("typ1","http://xmlns.oracle.com/adf/svc/types/");
       // soapEnvelope.addNamespaceDeclaration("soapenv","http://schemas.xmlsoap.org/soap/envelope/");
        
        //Authentication of soap request in header part
     //   SOAPHeader header = soapEnvelope.getHeader();             
        
      
        String authorization = DRMSyncPropertyV2.getInstance().getAuthorizationKey();
        MimeHeaders hd = soapMessage.getMimeHeaders();
         hd.addHeader("Authorization","Basic "+authorization);

        
        //Populate the body part of the soap request
            SOAPBody soapBody = soapEnvelope.getBody();
        
         //   QName bodyName = new QName("processCategory", "typ");
            SOAPElement processCategory = soapBody.addChildElement(DRMXMLStringUtil.CATEGORY_OPERATION_PROCESS_CATEGORY,"typ");
            
            
            
            SOAPElement changeOperation = processCategory.addChildElement(DRMXMLStringUtil.CATEGORY_CHANGE_OPERATION,"typ");
            changeOperation.addTextNode("Merge");

          //  SOAPElement category = processCategory.addChildElement("category","typ");
//            documentNumber.addTextNode("100");

          //  String CategoryName="",CategoryCode="",Description="",StartDate="",CatalogCode="",ParentCategoryCode="";
            
            for(int i=0;i<dataArray.length();i++)
            {
                
                JSONObject jobj=dataArray.getJSONObject(i);
                
                SOAPElement category = processCategory.addChildElement(DRMXMLStringUtil.CATEGORY,"typ");
                
                SOAPElement cat_name = category.addChildElement(DRMXMLStringUtil.CATEGORY_NAME,"item");
                cat_name.addTextNode(jobj.getString(DRMJSONStringUtil.CATEGORY_NAME));
                
                SOAPElement cat_code = category.addChildElement(DRMXMLStringUtil.CATEGORY_CODE,"item");
                cat_code.addTextNode(jobj.getString(DRMJSONStringUtil.CATEGORY_CODE));
                    
                SOAPElement desc = category.addChildElement(DRMXMLStringUtil.CATEGORY_DESCRIPTIOIN,"item");
                desc.addTextNode(jobj.getString(DRMJSONStringUtil.CATEGORY_DESCRIPTION));
                    
                SOAPElement stdate = category.addChildElement(DRMXMLStringUtil.START_DATE,"item");
                Date date = new Date();
                String modifiedDate= new SimpleDateFormat(DRMXMLStringUtil.DATE_FORMAT).format(date);
                stdate.addTextNode(modifiedDate);
                
                
                SOAPElement catalog_code = category.addChildElement(DRMXMLStringUtil.CATALOG_CODE,"item");
                catalog_code.addTextNode(CatalogName);
                        
                SOAPElement parent_cat_code = category.addChildElement(DRMXMLStringUtil.PARENT_CATEGORY_CODE,"item");
                parent_cat_code.addTextNode(jobj.getString(DRMJSONStringUtil.CATEGORY_PARENT_CATEGORY_CODE));
                                    
            } 
            
            SOAPElement processControl = processCategory.addChildElement(DRMXMLStringUtil.CATEGORY_PROCESS_CONTROL,"typ");
            SOAPElement returnMode = processControl.addChildElement(DRMXMLStringUtil.CATEGORY_RETURN_MODE,"typ1");
            returnMode.addTextNode("Full");
            
            SOAPElement partialFailureAllowed = processControl.addChildElement(DRMXMLStringUtil.CATEGORY_PARTIAL_FAILURE_ALLOWED,"typ1");
            partialFailureAllowed.addTextNode("false");
            
            soapMessage.saveChanges();
            
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapMessage.writeTo(out);
        String strMsg = new String(out.toByteArray());
        System.out.print(strMsg);
            System.out.println(soapMessage.toString());    
            
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            URL endpoint = new URL(DRMSyncPropertyV2.getInstance().getCatalogServiceURL());
            System.out.println("Calling.. "+DRMSyncPropertyV2.getInstance().getCatalogServiceURL());
            //Throw Exception if the recipe call times out
            
            //Recipe call
            SOAPMessage response = soapConnection.call(soapMessage, endpoint);
        
        out = new ByteArrayOutputStream();
        System.out.print("Response");
        response.writeTo(out);
        strMsg = new String(out.toByteArray());
        System.out.print(strMsg);
            
           
            //Parse the recipe response
            
            SOAPBody responseSoapBody = response.getSOAPBody();
            java.util.Iterator iterator = responseSoapBody.getChildElements();
            SOAPBodyElement bodyElement2 = (SOAPBodyElement)iterator.next();
            if(bodyElement2.getTagName().contains("Fault"))
                throw new SOAPException();
            String responseBody = bodyElement2.toString();
          //  System.out.print(responseBody);
            
        return true;
    }
    
    public static boolean invokeMergeCategoryService(JSONObject data,String CatalogName,String[] output) throws SOAPException,
                                                                                                 IOException,
                                                                                                 JSONException {
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(),loggerName,DRMSyncPropertyV2.getInstance().getLoggerID(),
                          "debug","Invoking MergeCategory through Orbit Microservice");
        //Create soap message for the web service request    
        MessageFactory messageFactory =  MessageFactory.newInstance();
        //SOAPConstants.SOAP_1_2_PROTOCOL
            //MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
        //KeyStoreAccessor keyStoreAccessor=new KeyStoreAccessor();
        //LookUpDAO lookUpDAO=new LookUpDAO();
        
        //Add Namespace Declaration
        soapEnvelope.addNamespaceDeclaration("soapenv","http://schemas.xmlsoap.org/soap/envelope/");
        soapEnvelope.addNamespaceDeclaration("typ","http://xmlns.oracle.com/apps/scm/productModel/catalogs/itemCatalogService/types/");
        soapEnvelope.addNamespaceDeclaration("item","http://xmlns.oracle.com/apps/scm/productModel/catalogs/itemCatalogService/");
        soapEnvelope.addNamespaceDeclaration("cat","http://xmlns.oracle.com/apps/scm/productModel/catalogs/flex/category/");
        soapEnvelope.addNamespaceDeclaration("typ1","http://xmlns.oracle.com/adf/svc/types/");
       // soapEnvelope.addNamespaceDeclaration("soapenv","http://schemas.xmlsoap.org/soap/envelope/");
        
        //Authentication of soap request in header part
     //   SOAPHeader header = soapEnvelope.getHeader();             
        
       String authorization = DRMSyncPropertyV2.getInstance().getAuthorizationKey();
       MimeHeaders hd = soapMessage.getMimeHeaders();
       hd.addHeader("Authorization","Basic "+authorization);

        //Populate the body part of the soap request
            SOAPBody soapBody = soapEnvelope.getBody();
        
         //   QName bodyName = new QName("processCategory", "typ");
            SOAPElement processCategory = soapBody.addChildElement(DRMXMLStringUtil.CATEGORY_OPERATION_MERGE_CATEGORY,"typ");
            
                JSONObject jobj=data;
                
                SOAPElement category = processCategory.addChildElement(DRMXMLStringUtil.CATEGORY,"typ");
                
                SOAPElement cat_name = category.addChildElement(DRMXMLStringUtil.CATEGORY_NAME,"item");
                cat_name.addTextNode(jobj.getString(DRMJSONStringUtil.CATEGORY_NAME));
                
                SOAPElement cat_code = category.addChildElement(DRMXMLStringUtil.CATEGORY_CODE,"item");
                cat_code.addTextNode(jobj.getString(DRMJSONStringUtil.CATEGORY_CODE));
                    
                SOAPElement desc = category.addChildElement(DRMXMLStringUtil.CATEGORY_DESCRIPTIOIN,"item");
                desc.addTextNode(jobj.getString(DRMJSONStringUtil.CATEGORY_DESCRIPTION));
                    
                SOAPElement stdate = category.addChildElement(DRMXMLStringUtil.START_DATE,"item");
                Date date = new Date();
                String modifiedDate= new SimpleDateFormat(DRMXMLStringUtil.DATE_FORMAT).format(date);
                stdate.addTextNode(modifiedDate);
                
                
                SOAPElement catalog_code = category.addChildElement(DRMXMLStringUtil.CATALOG_CODE,"item");
                catalog_code.addTextNode(CatalogName);
                        
                SOAPElement parent_cat_code = category.addChildElement(DRMXMLStringUtil.PARENT_CATEGORY_CODE,"item");
                parent_cat_code.addTextNode(jobj.getString(DRMJSONStringUtil.CATEGORY_PARENT_CATEGORY_CODE));
                                    
            
            soapMessage.saveChanges();
            
            System.out.println(soapMessage.toString());    
            
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
            URL endpoint = new URL(DRMSyncPropertyV2.getInstance().getInstance().getCatalogServiceURL());
            //Throw Exception if the recipe call times out
            
            //Recipe call
            SOAPMessage response = soapConnection.call(soapMessage, endpoint);
        
        
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            soapMessage.writeTo(out);
            String strMsg = new String(out.toByteArray());
            System.out.print(strMsg);
            
            
        out = new ByteArrayOutputStream();
        System.out.println("Response");
        response.writeTo(out);
        strMsg = new String(out.toByteArray());
        System.out.print(strMsg);
            
            //Parse the recipe response
            SOAPBody responseSoapBody = response.getSOAPBody();
            java.util.Iterator iterator = responseSoapBody.getChildElements();
            SOAPBodyElement bodyElement2 = (SOAPBodyElement)iterator.next();
            
            
            if(bodyElement2.getTagName().contains("Fault"))
            {
                
                java.util.Iterator iterator2 = bodyElement2.getChildElements();
                SOAPElement bodyElement4=(SOAPElement)iterator2.next();
                SOAPElement bodyElement3 = (SOAPElement)iterator2.next();
                output[0]="Failure";
                String errormssg=StringEscapeUtils.unescapeHtml(bodyElement3.getTextContent());
                output[1]=errormssg;
                throw new SOAPException();
            }
            String responseBody = bodyElement2.toString();
            System.out.print(responseBody);  
        return true;
    }
    

}