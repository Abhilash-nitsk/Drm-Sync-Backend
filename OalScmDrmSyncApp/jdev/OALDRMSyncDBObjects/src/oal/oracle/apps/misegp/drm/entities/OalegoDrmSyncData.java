package oal.oracle.apps.misegp.drm.entities;

import java.io.Serializable;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;

@Entity
@NamedQueries({ @NamedQuery(name = "OalegoDrmSyncData.findAll",
                            query = "select o from OalegoDrmSyncData o") })
@Table(name = "OALEGO_DRM_SYNC_DATA")
/*

To be uncommented once the PL/SQL Procedure is working

@NamedStoredProcedureQuery(name = "DRM_DATA_CHANGE", 
    procedureName = "OALEGO_DRM_SYNC_CHANGE_PKG.DRM_GET_CHANGE",                              
    parameters = {             
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "P_REFRESH_ID", type = Integer.class)
    }
)*/
public class OalegoDrmSyncData implements Serializable {
    private static final long serialVersionUID = -9017948365875199654L;
    @Column(name = "CATALOG_CODE", nullable = false, length = 240)
    private String catalogCode;
    @Column(name = "CATEGORY_CODE", nullable = false, length = 240)
    private String categoryCode;
    @Column(name = "CATEGORY_DESCRIPTION", length = 240)
    private String categoryDescription;
    @Column(name = "CATEGORY_NAME", length = 240)
    private String categoryName;
    @Column(name = "DISABLE_FLAG", length = 5)
    private String disableFlag;
    @Column(name = "END_DATE", length = 240)
    private String endDate;
    @Column(name = "IS_LEAF", length = 3)
    private String isLeaf;
    @Column(length = 10)
    private String levl;
    @Column(name = "PARENT_CATEGORY_CODE", length = 240)
    private String parentCategoryCode;
    @Column(name = "REFRESH_ID", length = 50)
    private BigDecimal refreshId;
    @Column(name = "PROCESSED_FLAG", length = 10)
    private String processedFlag;
    @Column(name = "COMMENTS", length = 500)
    private String comments;
    @Column(name = "START_DATE", length = 240)
    private String startDate;
    @Id
    @Column(name = "UNIQUE_ROW_ID", nullable = false)
    private BigDecimal uniqueRowId;

    public OalegoDrmSyncData() {
    }

    public OalegoDrmSyncData(String catalogCode, String categoryCode, String categoryDescription,
                                      String categoryName, String disableFlag, String endDate, String isLeaf,
                                      String levl, String parentCategoryCode, String processedFlag, String startDate,BigDecimal refreshId,
                                      BigDecimal uniqueRowId,String comments) {
        this.catalogCode = catalogCode;
        this.categoryCode = categoryCode;
        this.categoryDescription = categoryDescription;
        this.categoryName = categoryName;
        this.disableFlag = disableFlag;
        this.endDate = endDate;
        this.isLeaf = isLeaf;
        this.levl = levl;
        this.parentCategoryCode = parentCategoryCode;
        this.processedFlag = processedFlag;
        this.startDate = startDate;
        this.uniqueRowId = uniqueRowId;
        this.refreshId = refreshId;
        this.comments = comments;
    }

    public void setRefreshId(BigDecimal refreshId) {
        this.refreshId = refreshId;
    }

    public BigDecimal getRefreshId() {
        return refreshId;
    }

    public String getCatalogCode() {
        return catalogCode;
    }

    public void setCatalogCode(String catalogCode) {
        this.catalogCode = catalogCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }
    
    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }
    

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDisableFlag() {
        return disableFlag;
    }

    public void setDisableFlag(String disableFlag) {
        this.disableFlag = disableFlag;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }

    public String getLevl() {
        return levl;
    }

    public void setLevl(String levl) {
        this.levl = levl;
    }

    public String getParentCategoryCode() {
        return parentCategoryCode;
    }

    public void setParentCategoryCode(String parentCategoryCode) {
        this.parentCategoryCode = parentCategoryCode;
    }

    public String getProcessedFlag() {
        return processedFlag;
    }

    public void setProcessedFlag(String processedFlag) {
        this.processedFlag = processedFlag;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getUniqueRowId() {
        return uniqueRowId;
    }

    public void setUniqueRowId(BigDecimal uniqueRowId) {
        this.uniqueRowId = uniqueRowId;
    }
}
