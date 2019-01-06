package oal.oracle.apps.scm.drm;

import java.math.BigDecimal;

import java.util.HashMap;

import oal.util.logger.OalLogger;

public class DrmSyncWrapper implements Runnable {

    private String loggerName = DrmSyncWrapper.class.getName();


    String refreshId;
    DRMSyncPropertyV2.Catalog cat;

    public DrmSyncWrapper(String refreshId, DRMSyncPropertyV2.Catalog cat) {
        this.refreshId = refreshId;
        this.cat = cat;
    }

    @Override
    public void run() {

        OalLogger.sendLog(DRMSyncPropertyV2.getInstance().getLoggerFlowName(), loggerName,
                          DRMSyncPropertyV2.getInstance().getLoggerID(), "info",
                          "Starting sync for catalog " + cat.getValue());
        OALDRMSync drmsync = new OALDRMSync();

        // System.out.println("Yeay the wrapper called successfully");
        //BigDecimal v = new BigDecimal(String.valueOf(refreshId));
        drmsync.init(refreshId, cat);
    }


}
