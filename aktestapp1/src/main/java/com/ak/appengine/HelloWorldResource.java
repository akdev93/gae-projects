package com.ak.appengine;

/**
 * Created by akoneri on 10/21/14.
 */
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import com.ak.appengine.dao.ExpenseTrackerDao;
import com.ak.appengine.meta.Category;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;

@Path("/hello")
public class HelloWorldResource {

    @GET
    @Produces("application/json")
    public MyBean getMessage() {
       //return "{ \"message\" : \"hello world\" }";http://localhost:8080/aktestapp1/hello/
        return (new MyBean("hello world"));

    }

    @POST
    @Path("/expense")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveExpense(Expense expense) throws Exception{
        ExpenseTrackerDao dao = new ExpenseTrackerDao();
        String keyString = dao.saveExpense(expense);
        return Response.status(200).entity(keyString).build();

    }

    @POST
    @Path("/expense/openPeriod")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response openPeriod(String name) throws Exception {
        ExpenseTrackerDao dao = new ExpenseTrackerDao();
        dao.openPeriod(name);
        return Response.status(200).entity("ok").build();

    }

    @GET
    @Path("/expense/sample")
    public Response getSampleExpense() {
        Expense e = new Expense();
        e.setTransactionDate(new Date());
        e.setCost(12.5f);
        e.setCategory("lunch");
        e.setPeriod("OCT-2014");
        e.setNotes("subway card recharge");

        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(e).build();
    }

    @GET
    @Path("/expense/report/{period}")
//    public PeriodicReport getPeriodicReport(@PathParam("period")String period) {
    public Response getPeriodicReport(@PathParam("period")String period) {
        ExpenseTrackerDao dao = new ExpenseTrackerDao();
        List<com.ak.appengine.meta.Category> categories = dao.getCategoriesForPeriod(period);
        List<Variance> variances = new ArrayList<Variance>();
        PeriodicReport report = new PeriodicReport();
        report.setPeriod(period);

        int periodicLimit = 0;
        float periodicTotal = 0f;
        for(int i=0;i<categories.size();i++) {
            com.ak.appengine.meta.Category c = categories.get(i);
            List<Expense> expenses = dao.getRecordsForCategory(period,c.getName());
            float total = 0;
            for(int j=0;j<expenses.size();j++) {
                total = total + (expenses.get(j).getCost());
            }
            Variance v = new Variance();
            v.setCategory(c.getName());
            v.setTotal(total);
            v.setLimit(c.getLimit());
            periodicLimit = periodicLimit+c.getLimit();
            periodicTotal = periodicTotal+total;
            variances.add(v);
        }
        report.setVarianceList(variances);
        report.setLimit(periodicLimit);
        report.setTotal(periodicTotal);
        //return report;
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(report).build();
    }

    @GET
    @Path("/expense/query/{period}/{category}")
    public Response queryExpenseRecords(@PathParam("period") String period, @PathParam("category") String category) {

        System.out.println("Period  : "+period);
        System.out.println("Category : "+category);
        ExpenseTrackerDao dao = new ExpenseTrackerDao();
        //return dao.getRecordsForCategory(period, category);
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(dao.getRecordsForCategory(period, category)).build();

    }

    @GET
    @Path("/expense/{keyString}")
    public Response getExpenseRecord(@PathParam("keyString") String keyString) {
        ExpenseTrackerDao dao = new ExpenseTrackerDao();
        Expense expense = null;
        Response response;
        try {
            expense = dao.getExpenseRecord(keyString);
            if( expense == null)
                response = Response.status(404).entity("No expense record found with key "+keyString).type(MediaType.TEXT_PLAIN).build();
            else
                response = Response.status(200).entity(expense).type(MediaType.APPLICATION_JSON).build();
        }catch(IllegalArgumentException e){
            response = Response.status(400).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
        }catch(Exception e) {
            response = Response.status(500).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
        }

        return response;
    }

    @GET
    @Path("/expense/{keyString}/full")
    public Response getExpenseRecordWithChildren(@PathParam("keyString") String keyString) {
        ExpenseTrackerDao dao = new ExpenseTrackerDao();
        Expense expense = dao.getExpenseRecordWithChildren(keyString);
        return Response.status(200).type(MediaType.APPLICATION_JSON).entity(expense).build();
    }

    @POST
    @Path("/expense/{keyString}/adjust")
    public Response postAdjustment(@PathParam("keyString") String keyString, Adjustment adjustment) throws Exception {
        ExpenseTrackerDao dao = new ExpenseTrackerDao();
        Expense parentExpense = dao.getExpenseRecord(keyString);
        if(parentExpense != null) {
            float adjCost = adjustment.getAmount();
            adjustment.setParentExpenseKeyString(keyString);
            //validate adjCost <= parentExpense.getCost();
            String adjKey  = dao.saveAdjustment(parentExpense,adjustment);

            System.out.println("Adj key to be set in exp :"+adjKey);
            Expense deductionExpense = new Expense();
            copyExpense(parentExpense, deductionExpense);
            deductionExpense.setAdjustment(true);
            deductionExpense.setCost(-adjustment.getAmount());
            deductionExpense.setNotes("[adj] "+adjustment.getNotes());
            deductionExpense.setAdjustmentKeyEncoded(adjKey);

            saveExpense(deductionExpense);

            Expense movedExpense = new Expense();
            copyExpense(parentExpense, movedExpense);
            movedExpense.setCost(adjustment.getAmount());
            movedExpense.setPeriod(adjustment.getMoveToPeriod());
            movedExpense.setCategory(adjustment.getMoveToCategory());
            movedExpense.setAdjustment(true);
            movedExpense.setNotes("[adj] (from "+parentExpense.getPeriod()+" - "+parentExpense.getCategory()+") "+adjustment.getNotes());
            movedExpense.setTags(adjustment.getMoveToTags());
            movedExpense.setAdjustmentKeyEncoded(adjKey);
            movedExpense.setSubcategory1(adjustment.getMoveToSubcategory1());
            movedExpense.setSubcategory2(adjustment.getMoveToSubcategory2());

            saveExpense(movedExpense);

            adjustment.setDeductionExpenseKeyString(deductionExpense.getKeyEncoded());
            adjustment.setMovedExpenseKeyString(movedExpense.getKeyEncoded());
            String s = dao.saveAdjustment(adjustment);
            System.out.println("Adj key after update : "+s);


            return Response.status(200).entity("ok").build();


        }

        return Response.status(404).entity("["+keyString+"] not found").build();

    }

    private void copyExpense(Expense src, Expense trg) {
        trg.setCost(src.getCost());
        trg.setCategory(src.getCategory());
        trg.setNotes(src.getNotes());
        trg.setSubcategory1(src.getSubcategory1());
        trg.setSubcategory2(src.getSubcategory2());
        trg.setTags(src.getTags());
        trg.setTransactionDate(src.getTransactionDate());
        trg.setPeriod(src.getPeriod());

    }

    @GET
    @Path("/list")
    @Produces("application/json")
    public List<MyBean> getMessages() throws Exception {
       List<MyBean> list = new ArrayList<MyBean>();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


        Query q = new Query("MyBean");


        PreparedQuery pq = datastore.prepare(q);


        for (Entity result : pq.asIterable()) {
            String message = (String) result.getProperty("message");
            String number = (String) result.getProperty("number");
            MyBean m = new MyBean();
            m.setMessage(message);
            m.setNumber(number);
            list.add(m);

        }

        Key key = KeyFactory.createKey("Period", "OCT-2014");
        System.out.println("key id :"+key.getId());
        System.out.println("key name:"+key.getName());
        System.out.println("key : "+KeyFactory.keyToString(key));

        Entity e = datastore.get(key);

        System.out.println("Entity "+e);
        if(e!=null) {
            System.out.println("properties "+e.getProperties());
        }

        PreparedQuery pq2 = datastore.prepare(new Query(e.getKey()));

        for (Entity result : pq2.asIterable()) {
            System.out.println("Kind : "+result.getKind());
            System.out.println("Key Name : "+result.getKey().getName());
            System.out.println("name :"+result.getProperty("name"));


        }

       return list;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setMessage(MyBean msg) {
        System.out.println("Message "+msg.getMessage());
        System.out.println("Number "+msg.getNumber());

        Key messageKey = KeyFactory.createKey("MyBean", msg.getMessage());

        Entity entity = new Entity("MyBean", messageKey);
        entity.setProperty("message", msg.getMessage());
        entity.setProperty("number", msg.getNumber());
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(entity);


        return Response.status(200).entity("ok").build();
    }
}
