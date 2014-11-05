package com.ak.appengine;

import javax.xml.bind.annotation.*;

/**
 * Created by akoneri on 10/28/14.
 */
@XmlRootElement
public class Variance {

    private String category;
    private float total;
    private float limit;

    @XmlElement
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @XmlElement
    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    @XmlElement
    public float getLimit() {
        return limit;
    }

    public void setLimit(float limit) {
        this.limit = limit;
    }

    @XmlElement
    public float getDifference() {
       return getTotal() - getLimit();
    }
}
