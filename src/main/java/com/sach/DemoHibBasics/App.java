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
    		e_n.setF_name("A");
    		e_n.setL_name("B");
    		e_n.setM_name("C");
    		
    		Employee emp = new Employee();
    		emp.setName(e_n);
    		emp.setEid(3);      // Put value in database
//    		emp.setName("Sachin");    // Put value in database 
    		emp.setDep("IT");     // Put value in database
    		
    		Configuration con =  new Configuration().configure().addAnnotatedClass(Employee.class);
    		SessionFactory sf = con.buildSessionFactory();
    		Session ses = sf.openSession();
    		Transaction tx = ses.beginTransaction();
    		ses.save(emp);   // Put value in database
//     		emp =(Employee) ses.get(Employee.class, 1);  // Get data from DB
    		tx.commit();	
//    		System.out.println("************************");
//    		System.out.println(emp);  // Get data from DB
    }
}