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
@NamedQueries({ @NamedQuery(name = "OalTestItem.findAll", query = "select o from OalTestItem o") })
@Table(name = "OAL_TEST_ITEM")
public class OalTestItem implements Serializable {
    private static final long serialVersionUID = -5972623333813605159L;
    @Column(length = 10)
    private String cat;
    @Id
    @Column(name = "ITEM_NUMBER", nullable = false, length = 10)
    private String itemNumber;
    @Column(name = "PARENT_CAT", length = 10)
    private String parentCat;
    @Column(name = "PROCESS_FLAG")
    private BigDecimal processFlag;

    public OalTestItem() {
    }

    public OalTestItem(String cat, String itemNumber, String parentCat, BigDecimal processFlag) {
        this.cat = cat;
        this.itemNumber = itemNumber;
        this.parentCat = parentCat;
        this.processFlag = processFlag;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getParentCat() {
        return parentCat;
    }

    public void setParentCat(String parentCat) {
        this.parentCat = parentCat;
    }

    public BigDecimal getProcessFlag() {
        return processFlag;
    }

    public void setProcessFlag(BigDecimal processFlag) {
        this.processFlag = processFlag;
    }
}
