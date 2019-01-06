package oal.oracle.apps.scm.drm;

import java.io.File;

import java.util.Properties;

import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.json.JSONArray;
import org.json.JSONObject;

public class MailNotification {
    public MailNotification() {
        super();
    }
    public static void main(String args[])
    
        throws MessagingException {
        sendSMTPmail("OAL-MDM-DRMSYNC@oracle.com",new String[]{"vikas.vi.yadav@oracle.com","ashish.vardhan@oracle.com,","abhilash.agarwal@oracle.com"},null,null,null,"TEST","TEST",null);
    }
    
    public static String generateTableHTML(JSONArray arr) {
        StringBuilder sb=new StringBuilder();
        sb.append("<table>");
        sb.append("<tr>");
        sb.append("<th>Level</th>");
        sb.append("<th>Processed Categories</th>");
        sb.append("<th>Unprocessed Categories</th>");
        sb.append("<th>Errored Categories</th>");
        sb.append("<th>Pass Rate</th>");
      //  sb.append("<th>Representation</th>");
        sb.append("</tr>");
        for(int i=0;i<arr.length();i++)
        {
        sb.append("<tr>");
            sb.append("<td>");
            sb.append(arr.getJSONObject(i).getString("level"));
            sb.append("</td>");
            
            int processed=Integer.valueOf(arr.getJSONObject(i).getString("processed"));
            int unprocessed=Integer.valueOf(arr.getJSONObject(i).getString("unprocessed"));
            int errored=Integer.valueOf(arr.getJSONObject(i).getString("errored"));
            double passRate=processed/(double)(processed+unprocessed+errored);
            passRate*=100;
            int factor1=(processed*100)/(processed+unprocessed+errored);
            int factor2=(100*unprocessed)/(processed+unprocessed+errored);
            int factor3=(100*errored)/(processed+unprocessed+errored);
            
            sb.append("<td>");
            sb.append(processed);
            sb.append("</td>");
            
            sb.append("<td>");
            sb.append(unprocessed);
            sb.append("</td>");
            
            sb.append("<td>");
            sb.append(errored);
            sb.append("</td>");
            
            sb.append("<td>");
            sb.append(passRate);
            sb.append("</td>");
            
//            sb.append("<td>");
//            
//             sb.append("<svg width=");
//            sb.append(factor1);
//            sb.append(" height=10>");
//              sb.append("<rect width=");
//            sb.append(factor1); 
//            String s=" height=\"10\" style=\"fill:rgb(0,0,255);stroke-width:10;stroke:rgb(255,0,0)\" />\n" + 
//            "            </svg>";
//            sb.append(s);
//            
//            
//            sb.append("<svg width=");
//            sb.append(factor2);
//            sb.append(" height=10>");
//             sb.append("<rect width=");
//            sb.append(factor2);
//            String s1=" height=\"10\" style=\"fill:rgb(0,0,255);stroke-width:10;stroke:rgb(0,255,0)\" />\n" +
//            "            </svg>";
//            sb.append(s1);
//            
//            sb.append("<svg width=");
//            sb.append(factor3);
//            sb.append(" height=10>");
//             sb.append("<rect width=");
//            sb.append(factor3);
//            String s2=" height=\"10\" style=\"fill:rgb(0,0,255);stroke-width:10;stroke:rgb(255,255,0)\" />\n" +
//            "            </svg>";
//            sb.append(s2);
//            
//            
//            sb.append("</td>");
            
            
            
        sb.append("</tr>");
        }
        sb.append("<table>");
          
          return sb.toString();
    }
    public static void sendNotification(String mssgheader,JSONObject message, File attachment) throws MessagingException {
        
        String header = message.getString("header");
         String dateTime =message.getString("dateTime");
         String catalogCode =message.getString("catalogCode");
        String status = message.getString("status");
         String catalogName =message.getString("catalogName");
         String maxDepth =message.getString("maxDepth");
         String tableHeader =message.getString("tableHeader");
         
         StringBuilder body= new StringBuilder();
         body.append("<h2>"+header+"</h2><br>");
        body.append("DATE & TIME : "+dateTime+"<br>");
        body.append("STATUS : "+status+"<br>");
        body.append("CATALOG CODE : "+catalogCode+"<br>");
        body.append("CATALOG NAME : "+catalogName+"<br>");
        body.append("MAX DEPTH OF HIERARCHY : "+maxDepth+"<br>");
        body.append("<h3>"+tableHeader+"</h3>");
         
        
        
        String messages="<!DOCTYPE html>\n" + 
        "<html>\n" + 
        "<head>\n" + 
        "<style>\n" + 
        "table {\n" + 
        "    font-family: arial, sans-serif;\n" + 
        "    border-collapse: collapse;\n" + 
        "    width: 100%;\n" + 
        "}\n" + 
        "\n" + 
        "td, th {\n" + 
        "    border: 1px solid #dddddd;\n" + 
        "    text-align: left;\n" + 
        "    padding: 8px;\n" + 
        "}\n" + 
        "\n" + 
        "tr:nth-child(even) {\n" + 
        "    background-color: #dddddd;\n" + 
        "}\n" + 
        "</style>\n" + 
        "</head>\n" + 
        "<body>\n" + 
        body.toString()+
        generateTableHTML(message.getJSONArray("data"))
         + 
        "</body>\n" + 
        "</html>\n";
        //System.out.println(messages);
        sendSMTPmail("OAL-MDM-DRMSYNC@oracle.com",new String[]{"vikas.vi.yadav@oracle.com","abhilash.agarwal@oracle.com"},null,null,attachment,mssgheader,messages,null);
        //,"ashish.vardhan@oracle.com","abhilash.agarwal@oracle.com"
        //,"ashish.vardhan@oracle.com","abhilash.agarwal@oracle.com","vani.hymavathi@oracle.com"
    }
    public static void sendSimpleMail(String from,String[] to, String subject, String message) {
        try {
            sendSMTPmail(from, to, null, null, null, subject, message, null);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }

    /**
         * sendSMTPmail - send SMTP mail to one or more recipient(s).
         *
         * @param strFrom : From
         * @param strArrTo : To
         * @param fileAttachment : Attachment, if any
         * @param strSubject : Subject
         * @param strMessageText : Message Body
         * @param mailPriority : Priority
         * @throws MessagingException
         */
        private static void sendSMTPmail(String strFrom, String[] strArrTo, String[] strArrCC, String[] strArrBCC,
                                         File fileAttachment, String strSubject, String strMessageText,
                                         String mailPriority) throws MessagingException {

            int intLen = 0;
            // Create the multi-part
            final Multipart multipart = new MimeMultipart();
            // Get system properties
            final Properties props = System.getProperties();

            // Setup mail server
            String SMTP_HOST = "internal-mail-router.oracle.com";
            //props.put("mail.smtp.host", commonKeys.getSmtpHost());
            props.put("mail.smtp.host", SMTP_HOST);

            // Get session
            final Session session = Session.getDefaultInstance(props, null);

            // Define message
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(strFrom));
            if (strArrTo != null) {
                intLen = strArrTo.length;

                for (int i = 0; i < intLen; i++) {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(strArrTo[i]));
                }
            }

            if (strArrCC != null) {
                intLen = strArrCC.length;
                for (int i = 0; i < intLen; i++) {
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(strArrCC[i]));
                }
                strArrCC = null;
            }

            if (strArrBCC != null) {
                intLen = strArrBCC.length;
                for (int i = 0; i < intLen; i++) {
                    message.addRecipient(Message.RecipientType.BCC, new InternetAddress(strArrBCC[i]));
                }
                strArrBCC = null;
            }
            message.setHeader("X-Priority", mailPriority);
            message.setSubject(strSubject);
            // Create part one
            BodyPart messageBodyPart = new MimeBodyPart();
            // Fill the message
            
            messageBodyPart.setText(strMessageText);
            messageBodyPart.setContent(strMessageText, "text/html");
            // Add the first part
            multipart.addBodyPart(messageBodyPart);
            // Part two is attachment
            if (fileAttachment != null) {
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(fileAttachment);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(fileAttachment.getName());
                // Add the second part
                multipart.addBodyPart(messageBodyPart);
            }
            // Put parts in message
            message.setContent(multipart);
            // Send message
            Transport.send(message);

        }
    

}
