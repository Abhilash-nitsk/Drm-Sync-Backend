package oal.oracle.apps.misegp.drm.entities;

import java.io.Serializable;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({ @NamedQuery(name = "OalegoDrmDataChange.findAll", query = "select o from OalegoDrmDataChange o") })
@Table(name = "OALEGO_DRM_DATA_CHANGE")
public class OalegoDrmDataChange implements Serializable {
    private static final long serialVersionUID = -325926355946608966L;
    @Column(name = "CATALOG_CODE", nullable = false, length = 240)
    private String catalogCode;
    @Column(name = "CATEGORY_CODE", nullable = false, length = 240)
    private String categoryCode;
    @Column(name = "CATEGORY_DESCRIPTION", length = 240)
    private String categoryDescription;
    @Column(name = "CATEGORY_NAME", length = 240)
    private String categoryName;
    @Column(name = "END_DATE", length = 240)
    private String endDate;
    @Column(length = 10)
    private String levl;
    @Column(name = "PARENT_CATEGORY_CODE", length = 240)
    private String parentCategoryCode;
    @Column(name = "REFRESH_ID", nullable = false)
    private BigDecimal refreshId;
    @Column(name = "START_DATE", length = 240)
    private String startDate;
    @Id
    @Column(name = "UNIQUE_ROW_ID", nullable = false)
    private BigDecimal uniqueRowId;
    
    public OalegoDrmDataChange() {
    }

    public OalegoDrmDataChange(String catalogCode, String categoryCode, String categoryDescription, String categoryName,
                               String endDate, String levl, String parentCategoryCode, BigDecimal refreshId,
                               String startDate,BigDecimal uniqueRowId) {
        this.catalogCode = catalogCode;
        this.categoryCode = categoryCode;
        this.categoryDescription = categoryDescription;
        this.categoryName = categoryName;
        this.endDate = endDate;
        this.levl = levl;
        this.parentCategoryCode = parentCategoryCode;
        this.refreshId = refreshId;
        this.startDate = startDate;
        this.uniqueRowId = uniqueRowId;

    }

    public BigDecimal getUniqueRowId() {
        return uniqueRowId;
    }

    public void setUniqueRowId(BigDecimal uniqueRowId) {
        this.uniqueRowId = uniqueRowId;
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public BigDecimal getRefreshId() {
        return refreshId;
    }

    public void setRefreshId(BigDecimal refreshId) {
        this.refreshId = refreshId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
