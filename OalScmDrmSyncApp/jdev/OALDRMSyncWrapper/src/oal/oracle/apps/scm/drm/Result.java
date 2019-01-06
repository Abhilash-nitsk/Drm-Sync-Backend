package oal.oracle.apps.scm.drm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.sql.TIMESTAMP;

import java.sql.Timestamp;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import oal.util.logger.OalLogger;


public class Result {
    public Result() {
        super();
    }
    int refresh_id;
    String catalog_code ;
    String catalog_name ;
    Timestamp notified_time_stamp ;
    Timestamp start_time_stamp ;
    Timestamp completed_time_stamp ;
    int  errored_cat;
    int processed_cat ;
    int unprocessed_cat ;
    int max_depth;
    String status;

    public synchronized void pushToDB() throws SQLException, NamingException {
        Connection connection = initDb();

        PreparedStatement ps1 =
        connection.prepareStatement("insert into oalego_drm_sync_record (refresh_id,catalog_code,catalog_name,notified_time_stamp," +
                                    "start_time_stamp,completed_time_stamp,errored_cat,unprocessed_cat,max_depth,status) values ("+
                                    this.refresh_id+","+this.catalog_code+","+this.catalog_name+","+this.notified_time_stamp+","+
                                    this.start_time_stamp+","+this.completed_time_stamp+","+this.errored_cat+","+this.processed_cat+","+this.unprocessed_cat+
                                    ","+this.max_depth+","+this.status+")");

        ps1.executeUpdate();
        connection.close();
    }
    

    private synchronized Connection initDb() throws NamingException, SQLException {

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



    

    public void setRefresh_id(int refresh_id) {
        this.refresh_id = refresh_id;
    }

    public int getRefresh_id() {
        return refresh_id;
    }

    public void setCatalog_code(String catalog_code) {
        this.catalog_code = catalog_code;
    }

    public String getCatalog_code() {
        return catalog_code;
    }

    public void setCatalog_name(String catalog_name) {
        this.catalog_name = catalog_name;
    }

    public String getCatalog_name() {
        return catalog_name;
    }

    public void setNotified_time_stamp(Timestamp notified_time_stamp) {
        this.notified_time_stamp = notified_time_stamp;
    }

    public Timestamp getNotified_time_stamp() {
        return notified_time_stamp;
    }

    public void setStart_time_stamp(Timestamp start_time_stamp) {
        this.start_time_stamp = start_time_stamp;
    }

    public Timestamp getStart_time_stamp() {
        return start_time_stamp;
    }

    public void setCompleted_time_stamp(Timestamp completed_time_stamp) {
        this.completed_time_stamp = completed_time_stamp;
    }

    public Timestamp getCompleted_time_stamp() {
        return completed_time_stamp;
    }

    public void setErrored_cat(int errored_cat) {
        this.errored_cat = errored_cat;
    }

    public int getErrored_cat() {
        return errored_cat;
    }

    public void setProcessed_cat(int processed_cat) {
        this.processed_cat = processed_cat;
    }

    public int getProcessed_cat() {
        return processed_cat;
    }

    public void setUnprocessed_cat(int unprocessed_cat) {
        this.unprocessed_cat = unprocessed_cat;
    }

    public int getUnprocessed_cat() {
        return unprocessed_cat;
    }

    public void setMax_depth(int max_depth) {
        this.max_depth = max_depth;
    }

    public int getMax_depth() {
        return max_depth;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}
