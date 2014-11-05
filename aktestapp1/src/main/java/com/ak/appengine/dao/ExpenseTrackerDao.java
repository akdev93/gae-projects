package com.ak.appengine.dao;

import java.lang.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import com.ak.appengine.*;
import com.ak.appengine.meta.*;

import com.google.appengine.api.datastore.*;

/**
 * Created by akoneri on 10/26/14.
 */
public class ExpenseTrackerDao {


    public void openPeriod(String name) {
        CategoryLimit limits = new CategoryLimit();
        Entity entity1 = new Entity("Period", name);
        entity1.setProperty("name", name);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(entity1);
        System.out.println("entity1 key ID  : "+entity1.getKey().getId());
        System.out.println("entity1 key Name: "+entity1.getKey().getName());

        //String[] categories = {"eat-out", "lunch", "misc", "coffee","indian-groceries", "groceries", "costco"};
        String[] categories = limits.getCategories();
        for(int i=0;i<categories.length;i++) {
            Entity entity = new Entity("Category", name+"."+categories[i], entity1.getKey());
            entity.setProperty("name",categories[i]);
            entity.setProperty("canonicalName", name + "." + categories[i]);
            entity.setProperty("limit", limits.getCategoryLimit(categories[i]));
            datastore.put(entity);
        }

    }

    public List<com.ak.appengine.meta.Category> getCategoriesForPeriod(String period) {
        Key periodKey = KeyFactory.createKey("Period", period);
        List<com.ak.appengine.meta.Category> categories  = new ArrayList<com.ak.appengine.meta.Category>();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(new Query(periodKey));
        for (Entity result : pq.asIterable()) {
            System.out.println("Kind : " + result.getKind());
            System.out.println("Key Name : " + result.getKey().getName());
            if (!result.getKind().equals("Category")) {
                continue;
            }
            com.ak.appengine.meta.Category c = new com.ak.appengine.meta.Category();
            c.setName((String) result.getProperty("name"));
            c.setLimit((int)((Long) result.getProperty("limit")).longValue());
            categories.add(c);
        }
        return categories;
    }

    public List<Expense> getRecordsForCategory(String period, String category) {

        Key periodKey = KeyFactory.createKey("Period", period);
        Key categoryKey = KeyFactory.createKey(periodKey, "Category", period+"."+category );
        System.out.println("Category Key :"+KeyFactory.keyToString(categoryKey));
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery pq = datastore.prepare(new Query(categoryKey));
        List<Expense> list = new ArrayList<Expense>();

        for (Entity result : pq.asIterable()) {
            System.out.println("Kind : "+result.getKind());
            System.out.println("Key Name : "+result.getKey().getName());

            System.out.println("Key  : "+result.getKey());
            System.out.println("Key  : "+KeyFactory.keyToString( result.getKey()));



            if(!result.getKind().equals("Expense")) {
                continue;
            }
            Expense e = new Expense();
            populateExpenseFromEntity(result,e);

            list.add(e);



        }

        return list;
    }

    public String  saveExpense(Expense expense) throws Exception{

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Key key = KeyFactory.createKey("Period", expense.getPeriod());
        Entity periodEntity = datastore.get(key);
        System.out.println("Got periodEntity "+periodEntity);

        key = KeyFactory.createKey(key,"Category", expense.getPeriod()+"."+expense.getCategory());
        Entity categoryEntity = datastore.get(key);
        System.out.println("Got category entity "+categoryEntity);

        Entity entity = new Entity("Expense",categoryEntity.getKey());
        entity.setProperty("cost", expense.getCost());
        entity.setProperty("period", expense.getPeriod());
        entity.setProperty("transactionDate", expense.getTransactionDate());
        entity.setProperty("category", expense.getCategory());
        entity.setProperty("isAdjustment", expense.isAdjustment());
        entity.setProperty("subcategory1", expense.getSubcategory1());
        entity.setProperty("subcategory2", expense.getSubcategory2());
        entity.setProperty("tags", expense.getTags());
        entity.setProperty("notes", expense.getNotes());
        entity.setProperty("adjustmentKeyEncoded", expense.getAdjustmentKeyEncoded());
        Key k = datastore.put(entity);
        expense.setKeyEncoded(KeyFactory.keyToString(k));
        return KeyFactory.keyToString(k);


    }

    public Expense getExpenseRecord(String keyString) {
        Key k = KeyFactory.stringToKey(keyString);
        DatastoreService  ds = DatastoreServiceFactory.getDatastoreService();
        Entity entity = null;
        try {
            entity = ds.get(k);
        }
        catch(EntityNotFoundException enfE) {
            enfE.printStackTrace();
            entity= null;
        }
        Expense expense = null;
        if(entity != null) {
            expense = new Expense();
            populateExpenseFromEntity(entity,expense);
        }

        return expense;

    }

    public Expense getExpenseRecordWithChildren(String keyString) {
        Key k = KeyFactory.stringToKey(keyString);
        DatastoreService  ds = DatastoreServiceFactory.getDatastoreService();
        Expense expense = null;
        Entity entity = null;
        List<Adjustment> adjustmentList = new ArrayList<Adjustment>();
        PreparedQuery pq = ds.prepare(new Query(k));
        for (Entity result : pq.asIterable()) {
            System.out.println("Kind : " + result.getKind());
            System.out.println("Key Name : " + result.getKey().getName());
            if (result.getKind().equals("Expense")) {
                expense = new Expense();
                populateExpenseFromEntity(result, expense);
            }
            if (result.getKind().equals("Adjustment")) {
                Adjustment adjustment = new Adjustment();
                populateAdjustmentFromEntity(result,adjustment);
                adjustmentList.add(adjustment);
            }
        }

        if(expense != null)
            expense.setAdjustmentList(adjustmentList);
        return expense;

    }

    public String saveAdjustment(Expense expense, Adjustment adjustment) throws EntityNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Key key = KeyFactory.stringToKey(expense.getKeyEncoded());

        Entity entity = new Entity("Adjustment", key);
        entity.setProperty("notes",adjustment.getNotes());
        entity.setProperty("amount",adjustment.getAmount());
        entity.setProperty("moveToCategory",adjustment.getMoveToCategory());
        entity.setProperty("moveToPeriod", adjustment.getMoveToPeriod());
        entity.setProperty("parentExpenseKeyString", adjustment.getParentExpenseKeyString());
        entity.setProperty("movedExpenseKeyString", adjustment.getMovedExpenseKeyString());
        entity.setProperty("deductionExpenseKeyString", adjustment.getDeductionExpenseKeyString());

        Key k = datastore.put(entity);
        adjustment.setKeyEncoded(KeyFactory.keyToString(k));
        System.out.println("Adj key encoded is : "+adjustment.getKeyEncoded());
        return KeyFactory.keyToString(k);
    }

    public String saveAdjustment(Adjustment adjustment) {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        System.out.println("Adj update with key : "+adjustment.getKeyEncoded());

        Key key = KeyFactory.stringToKey(adjustment.getKeyEncoded());
        System.out.println("Pre update key adj :"+key);

        //Entity entity = new Entity("Adjustment", key);
        Entity entity = new Entity(key);

        entity.setProperty("notes",adjustment.getNotes());
        entity.setProperty("amount",adjustment.getAmount());
        entity.setProperty("moveToCategory",adjustment.getMoveToCategory());
        entity.setProperty("moveToPeriod", adjustment.getMoveToPeriod());
        entity.setProperty("parentExpenseKeyString", adjustment.getParentExpenseKeyString());
        entity.setProperty("movedExpenseKeyString", adjustment.getMovedExpenseKeyString());
        entity.setProperty("deductionExpenseKeyString", adjustment.getDeductionExpenseKeyString());

        Key k = datastore.put(entity);
        System.out.println("Post update key adj :"+k);
        adjustment.setKeyEncoded(KeyFactory.keyToString(k));
        return KeyFactory.keyToString(k);

    }

    private Expense populateExpenseFromEntity(Entity entity, Expense expense) {
        expense.setPeriod((String)entity.getProperty("period"));
        expense.setTransactionDate((Date)entity.getProperty("transactionDate"));
        expense.setCategory((String)entity.getProperty("category"));
        expense.setAdjustment(((Boolean)entity.getProperty("isAdjustment")).booleanValue());
        expense.setNotes((String)entity.getProperty("notes"));
        expense.setCost((float)((Double)entity.getProperty("cost")).doubleValue());
        expense.setTags((String)entity.getProperty("tags"));
        expense.setSubcategory1((String)entity.getProperty("subcategory1"));
        expense.setSubcategory2((String)entity.getProperty("subcategory2"));
        expense.setKeyEncoded(KeyFactory.keyToString(entity.getKey()));
        expense.setAdjustmentKeyEncoded((String)entity.getProperty("adjustmentKeyEncoded"));
        return expense;
    }

    private Adjustment populateAdjustmentFromEntity(Entity entity, Adjustment adjustment) {
        adjustment.setAmount((float)((Double)entity.getProperty("amount")).doubleValue());
        adjustment.setMoveToCategory((String)entity.getProperty("moveToCategory"));
        adjustment.setMoveToPeriod((String)entity.getProperty("moveToPeriod"));
        adjustment.setNotes((String)entity.getProperty("notes"));
        adjustment.setParentExpenseKeyString((String)entity.getProperty("parentExpenseKeyString"));
        adjustment.setDeductionExpenseKeyString((String)entity.getProperty("deductionExpenseKeyString"));
        adjustment.setMovedExpenseKeyString((String)entity.getProperty("movedExpenseKeyString"));
        adjustment.setKeyEncoded(KeyFactory.keyToString(entity.getKey()));
        return adjustment;
    }
}
