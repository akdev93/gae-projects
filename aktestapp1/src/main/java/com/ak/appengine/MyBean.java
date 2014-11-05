package com.ak.appengine;

import javax.xml.bind.annotation.*;

/**
 * Created by akoneri on 10/21/14.
 */
@XmlRootElement
public class MyBean {
    private String message;

    private String number;

    public MyBean() {}

    public MyBean(String message) {
        setMessage(message);
    }
    @XmlElement
    public String getMessage() { return this.message;}

    public void setMessage(String message) { this.message = message;}

    @XmlElement
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
