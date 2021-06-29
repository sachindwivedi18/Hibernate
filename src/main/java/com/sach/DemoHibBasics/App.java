package com.sach.DemoHibBasics;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class App 
{
    public static void main( String[] args )
    {
    		EmployeeName e_n = new EmployeeName();
    		e_n.setF_name("Arun");
    		e_n.setL_name("Dwivedi");
    		e_n.setM_name("Prakash");
    		
    		Employee emp = new Employee();
    		emp.setName(e_n);
    		emp.setEid(108520);      // Put value in database
//    		emp.setName("Sachin");    // Put value in database 
    		emp.setDep("IT");     // Put value in database
    		
    		Configuration con =  new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(Employee.class);
    		SessionFactory sf = con.buildSessionFactory();
    		Session ses = sf.openSession(); // Here you wll get object of Session
    		Transaction tx = ses.beginTransaction();
    		ses.save(emp);   // Put value in database
    		// Get vs Load
    		System.out.println("************************");
    		System.out.println("Get vs Load");
     		emp =(Employee) ses.load(Employee.class, 71);  // Get data from DB
    		tx.commit();	
//    		System.out.println(emp);  // Get data from DB
    }
}