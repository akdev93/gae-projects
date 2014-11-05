package com.ak.appengine;

import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * Created by akoneri on 10/28/14.
 */
@XmlRootElement
public class PeriodicReport {
    private String period;
    private List<Variance> varianceList;
    private float limit;
    private float total;


    @XmlElement
    public float getDifference() {
        return getTotal() - getLimit();
    }


    @XmlElement
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @XmlElement
    public List<Variance> getVarianceList() {
        return varianceList;
    }

    public void setVarianceList(List<Variance> varianceList) {
        this.varianceList = varianceList;
    }

    @XmlElement
    public float getLimit() {
        return limit;
    }

    public void setLimit(float limit) {
        this.limit = limit;
    }

    @XmlElement
    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
