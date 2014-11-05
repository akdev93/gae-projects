package com.ak.appengine.meta;

import java.util.*;
/**
 * Created by akoneri on 10/26/14.
 */
public class CategoryLimit {
  private Map<String,Integer> map = new HashMap<String,Integer>();

    public CategoryLimit() {
        map.put("mortgage", new Integer(1200));
        map.put("eat-out", new Integer(300));
        map.put("cell-phones", new Integer(165));
        map.put("vonage", new Integer(38));
        map.put("heating", new Integer(60));
        map.put("electric", new Integer(52));
        map.put("water", new Integer(20));
        map.put("waste", new Integer(20));
        map.put("cable-internet", new Integer(140));
        map.put("lunch", new Integer(120));
        map.put("coffee", new Integer(100));
        map.put("indian-groceries", new Integer(100));
        map.put("groceries", new Integer(50));
        map.put("costco", new Integer(50));
        map.put("gym", new Integer(38));
        map.put("gas", new Integer(140));
        map.put("travel", new Integer(120));
        map.put("misc", new Integer(0));
    }
    public int getCategoryLimit(String categoryName) {
        Integer integer = map.get(categoryName);
        int limit = 0; //assume 0 as limit if not defined.
        if(integer != null) {
            limit = integer.intValue();
        }
        return limit;
    }

    public String[] getCategories() {
        java.util.Set keys = map.keySet();
        return (String[])keys.toArray(new String[0]);

    }
}
