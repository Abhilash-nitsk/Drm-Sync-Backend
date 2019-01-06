package oal.oracle.apps.scm.drm;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;

import oal.util.logger.OalLogger;

public class DRMDataSync {
    public DRMDataSync() {
        super();
    }

    private String loggerName = DRMDataSync.class.getName();

    public static void main(String args[]) throws SQLException, NamingException, InterruptedException {
        //new DRMDataSync().sync();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new DRMDataSync().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "1");
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new DRMDataSync().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "2");
//        Thread t2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    new DRMDataSync().sync();
//                } catch (Exception e) {
//                }
//            }
//        }, "3");
//        Thread t3 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    new DRMDataSync().sync();
//                } catch (Exception e) {
//                }
//            }
//        }, "4");
        t.start();
        // Thread.sleep(10);

        t1.start();

        //Thread.sleep(10);
//        t2.start();
//        //Thread.sleep(10);
//        t3.start();


    }
    static Object lock = new Object();
    static Object lock1 = new Object();
    static List<String> refreshids = new ArrayList();

    public void sync() throws SQLException, NamingException, Exception {

        System.out.println("Invoking sync at time:" + getCurrentTimeStamp() + " by " +
                           Thread.currentThread().getName());

        // System.out.println("Number of session: "+noofsessions);


        String newRefreshId = null;

        synchronized (lock1) {

            System.out.println("Lock1 Acquired at:" + getCurrentTimeStamp() + " with HCode " + lock.hashCode() + " by" +
                               Thread.currentThread().getName());

            DRMSyncPropertyV2.init();
            String dateTime = getCurrentTimeStamp();
            DRMViewUpdatedNotification(dateTime);

            newRefreshId = generateNewRefreshId();
            refreshids.add(newRefreshId);
            System.out.println(Thread.currentThread().getName() + " generated new refreshid " + newRefreshId);
            updatelastRefreshId(newRefreshId);
            System.out.println(Thread.currentThread().getName() + " updated new refreshid " + newRefreshId);

            updatelastRefreshTimeStamp(dateTime);
            addToRecord(newRefreshId, dateTime);
        }


        if (newRefreshId == null)
            throw new Exception();

        seedDataToStageTable(newRefreshId);

        synchronized (lock) {

            while (!newRefreshId.equals(refreshids.get(0))) {
                System.out.println(Thread.currentThread().getName() + " tried to acquire lock to process refresh id " +
                                   newRefreshId);
                lock.wait();
            }


            System.out.println("Lock Acquired at:" + getCurrentTimeStamp() + " with HCode " + lock.hashCode() + " by" +
                               Thread.currentThread().getName() + " to processing refresh id " + newRefreshId);

            updateCurrentRefreshId(newRefreshId);
            seedDeltaTable(newRefreshId);
            startSyncforAllCatalogs(newRefreshId);

           
            refreshids.remove(0);
            System.out.println("lock released at:" + getCurrentTimeStamp() + " by " + Thread.currentThread().getName());
            lock.notifyAll();

        }
        //
    }

    private String getCurrentTimeStamp() {
        return (new Timestamp(System.currentTimeMillis())).toString();
    }

    private void DRMViewUpdatedNotification(String dateTime) {

        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "info",
                          "Sending DRMView Update Notification..");


        String message = "DRM VIEW HAS BEEN REFRESHED AT " + dateTime;
        MailNotification.sendSimpleMail("OAL-MDM-DRMSYNC@oracle.com", new String[] { "vikas.vi.yadav@oracle.com" },
                                        "DRM View Updated", message);
    }


    private void updateCurrentRefreshId(String refreshid) throws SQLException, NamingException {
        DRMSyncPropertyV2.getInstance().updateCurrentRefreshId(refreshid);

    }

    private String generateNewRefreshId() throws SQLException, NamingException {


        DRMSyncPropertyV2.init();
        int refreshid = Integer.parseInt(DRMSyncPropertyV2.getInstance().getLastRefreshId()) + 10;

        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "info",
                          "Generated new refresh id " + refreshid);

        return String.valueOf(refreshid);
    }

    private void seedDataToStageTable(String newRefreshId) throws ClassNotFoundException, SQLException {
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "info",
                          "Copying data from DRMView to paas stage table");
        ////

       
            Class.forName(DRMSyncPropertyV2.getInstance().getDBDriver());
            Connection dbConnection =
                DriverManager.getConnection(DRMSyncPropertyV2.getInstance().getDBURL(),
                                            DRMSyncPropertyV2.getInstance().getDBUser(),
                                            DRMSyncPropertyV2.getInstance().getDBPassword());
            PreparedStatement preparedStatement =
                dbConnection.prepareStatement(DRMSyncPropertyV2.getInstance().getStageTableSeedingProcedure());
            preparedStatement.setObject(1, Integer.parseInt(newRefreshId));
            preparedStatement.execute();
            dbConnection.close();
            //dbConnection.setAutoCommit(false);
            System.out.println("\n Datasource Fetched");
       
        ///


        System.out.println(Thread.currentThread().getName() + " copying data " + newRefreshId);
//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//        }

        System.out.println(Thread.currentThread().getName() + " copying data finished " + newRefreshId);

    }

    private synchronized void startSyncforAllCatalogs(String newRefreshId) {

        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "info", "Starting sync for catalogs.");

        ExecutorService executorService = Executors.newFixedThreadPool(DRMSyncPropertyV2.Catalog
                                                                                        .values()
                                                                                        .length);

        // List<Thread> list=new ArrayList();
        for (DRMSyncPropertyV2.Catalog cat : DRMSyncPropertyV2.Catalog.values()) {
            DrmSyncWrapper wrapper = new DrmSyncWrapper(newRefreshId, cat);
            executorService.execute(wrapper);
            //            Thread thread = new Thread(wrapper);
            //            list.add(thread);
            //            thread.start();

        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //        while(list.get(0).isAlive() || list.get(1).isAlive() || list.get(2).isAlive()) {
        //            try {
        //                Thread.sleep(10000);
        //            } catch (InterruptedException e) {
        //                e.printStackTrace();
        //            }
        //        }
        System.out.println("Finished");

        //Thread t1=new Thread()
    }

    private void updatelastRefreshId(String newRefreshId) throws SQLException, NamingException {
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "info", "Updating last refreshed id");

        DRMSyncPropertyV2.getInstance().updatelastRefreshId(newRefreshId);
    }

    private void updatelastRefreshTimeStamp(String timestamp) throws SQLException, NamingException {
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "info", "Updating last refreshed timestamp");

        DRMSyncPropertyV2.getInstance().updatelastRefreshTimeStamp(timestamp);
    }

    private void seedDeltaTable(String newRefreshId) {
        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "info", "Populating Delta Table..");

        findDelta(newRefreshId);

    }

    public void findDelta(String refreshId) {

        try {
            Class.forName(DRMSyncPropertyV2.getInstance().getDBDriver());
            Connection dbConnection =
                DriverManager.getConnection(DRMSyncPropertyV2.getInstance().getDBURL(),
                                            DRMSyncPropertyV2.getInstance().getDBUser(),
                                            DRMSyncPropertyV2.getInstance().getDBPassword());
            PreparedStatement preparedStatement =
                dbConnection.prepareStatement(DRMSyncPropertyV2.getInstance().getDBProcedure());
            preparedStatement.setObject(1, Integer.parseInt(refreshId));
            preparedStatement.execute();
            dbConnection.close();
            //dbConnection.setAutoCommit(false);
            System.out.println("\n Datasource Fetched");
        } catch (Exception e) {
            
            e.printStackTrace();
        
        }
    }

    private void addToRecord(String newRefreshId, String timestamp) {
        
    }
}
