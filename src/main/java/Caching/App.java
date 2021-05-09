package Caching;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class App {
	public static void main(String[] args) {
/*
		Employee emp1 = null;
		Employee emp = new Employee();
		emp.setEid(3); // Put value in database
		emp.setName("Sachin"); // Put value in database
		emp.setDep("IT"); // Put value in database
		*/

		Configuration con = new Configuration().configure().addAnnotatedClass(Employee.class);
		SessionFactory sf = con.buildSessionFactory();

		/*
		Session ses = sf.openSession();
		ses.beginTransaction();
		ses.save(emp); // Put value in database
		System.out.println("****************************");
		ses.getTransaction().commit();
		ses.close();
		*/

		// Ist level cache don't work in different session , for that 2nd level caching needed to be
		// implemented 
		Session s1 = sf.openSession();
		s1.beginTransaction();
		System.out.println(s1.get(Employee.class, 3));	
//		s1.load(Employee.class, 3);   Load will give u object only when u need to, for eg if object is required in next line
		s1.getTransaction().commit();
		s1.close();

		Session s2 = sf.openSession();
		s2.beginTransaction();
		System.out.println(s2.get(Employee.class, 3));
		s2.getTransaction().commit();
		s2.close();
		
		// Can make normal SQL query or HQL query
	}
}