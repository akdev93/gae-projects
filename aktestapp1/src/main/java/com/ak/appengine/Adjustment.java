package com.ak.appengine;

import javax.xml.bind.annotation.*;

/**
 * Created by akoneri on 11/1/14.
 */
@XmlRootElement
public class Adjustment {
    private String parentExpenseKeyString;
    private float amount;
    private String moveToPeriod;
    private String moveToCategory;
    private String notes;
    private String keyEncoded;

    private String deductionExpenseKeyString;
    private String movedExpenseKeyString;
    private String moveToTags;
    private String moveToSubcategory1;
    private String moveToSubcategory2;

    @XmlElement
    public String getParentExpenseKeyString() {
        return parentExpenseKeyString;
    }

    public void setParentExpenseKeyString(String expense) {
        this.parentExpenseKeyString = expense;
    }

    @XmlElement
    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @XmlElement
    public String getMoveToPeriod() {
        return moveToPeriod;
    }

    public void setMoveToPeriod(String moveToPeriod) {
        this.moveToPeriod = moveToPeriod;
    }

    @XmlElement
    public String getMoveToCategory() {
        return moveToCategory;
    }

    public void setMoveToCategory(String moveToCategory) {
        this.moveToCategory = moveToCategory;
    }

    @XmlElement
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @XmlElement
    public String getDeductionExpenseKeyString() {
        return deductionExpenseKeyString;
    }

    public void setDeductionExpenseKeyString(String deductionExpenseKeyString) {
        this.deductionExpenseKeyString = deductionExpenseKeyString;
    }

    @XmlElement
    public String getMovedExpenseKeyString() {
        return movedExpenseKeyString;
    }

    public void setMovedExpenseKeyString(String movedExpenseKeyString) {
        this.movedExpenseKeyString = movedExpenseKeyString;
    }

    @XmlElement
    public String getKeyEncoded() {
        return keyEncoded;
    }

    public void setKeyEncoded(String keyEncoded) {
        this.keyEncoded = keyEncoded;
    }

    @XmlElement
    public String getDeductionExpenseRef() {
        String deductionExpenseRef = "";
        if(getDeductionExpenseKeyString() != null)
            deductionExpenseRef = "/expense/"+getDeductionExpenseKeyString();

        return deductionExpenseRef;
    }

    public String getMovedExpenseRef() {
        String movedExpenseRef = "";
        if(getMovedExpenseKeyString() != null)
            movedExpenseRef = "/expense/"+getMovedExpenseKeyString();

        return movedExpenseRef;
    }

    @XmlElement
    public String getParentExpenseRef() {
        return "/expense/"+getParentExpenseKeyString();
    }

    @XmlElement
    public String getMoveToTags() {
        return moveToTags;
    }

    public void setMoveToTags(String moveToTags) {
        this.moveToTags = moveToTags;
    }

    @XmlElement
    public String getMoveToSubcategory1() {
        return moveToSubcategory1;
    }

    public void setMoveToSubcategory1(String moveToSubcategory1) {
        this.moveToSubcategory1 = moveToSubcategory1;
    }

    @XmlElement
    public String getMoveToSubcategory2() {
        return moveToSubcategory2;
    }

    public void setMoveToSubcategory2(String moveToSubcategory2) {
        this.moveToSubcategory2 = moveToSubcategory2;
    }
}
