package com.ak.appengine;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import javax.xml.bind.annotation.*;



/**
 * Created by akoneri on 10/22/14.
 */

@XmlRootElement
public class Expense {

    private float cost;
    private String category;
    private Date transactionDate;
    private String period;
    private String subcategory1;
    private String subcategory2;
    private String notes;
    private boolean adjustment;
    private String tags;
    private String keyEncoded;
    private String adjustmentKeyEncoded;
    private List<Adjustment> adjustmentList;


    @XmlElement
    public List<Adjustment> getAdjustmentList() {
        return adjustmentList;
    }

    public void setAdjustmentList(List<Adjustment> adjustmentList) {
        this.adjustmentList = adjustmentList;
    }

    @XmlElement
    public String getAdjustmentKeyEncoded() {
        return adjustmentKeyEncoded;
    }

    public void setAdjustmentKeyEncoded(String adjustmentKeyEncoded) {
        this.adjustmentKeyEncoded = adjustmentKeyEncoded;
    }

    @XmlElement
    public String getKeyEncoded() {
        return keyEncoded;
    }



    public void setKeyEncoded(String keyEncoded) {
        this.keyEncoded = keyEncoded;
    }

    @XmlElement
    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    @XmlElement
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @XmlElement
    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    @XmlElement
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @XmlElement
    public String getSubcategory1() {
        return subcategory1;
    }

    public void setSubcategory1(String subcategory1) {
        this.subcategory1 = subcategory1;
    }

    @XmlElement
    public String getSubcategory2() {
        return subcategory2;
    }

    public void setSubcategory2(String subcategory2) {
        this.subcategory2 = subcategory2;
    }

    @XmlElement
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @XmlElement
    public boolean isAdjustment() {
        return adjustment;
    }

    public void setAdjustment(boolean adjustment) {
        this.adjustment = adjustment;
    }

    @XmlElement
    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
