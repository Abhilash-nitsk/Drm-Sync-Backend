package oal.oracle.apps.misegp.drm.service;

import java.math.BigDecimal;

import javax.ejb.Stateless;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import java.util.HashMap;

import javax.ws.rs.QueryParam;

import oal.oracle.apps.scm.drm.DRMDataSync;

@Stateless
@Path("Wrapper")
@SuppressWarnings("oracle.jdeveloper.webservice.rest.broken-resource-error")
public class SyncWrapperService {
    public SyncWrapperService() {
        //super(OALDRMSync.class);
    }


    @GET
    @Path("/syncCategories")
    public String syncData() {
        System.out.println(" in syncCategories Service");
        try {

            DRMDataSync drm = new DRMDataSync();
            drm.sync();
            //Thread thread = new Thread(wrapper);
            //thread.start();

        } catch (Exception e) {
            return "Failed due to " + e.getStackTrace().toString();
        }
        return "Success";
    }
}
