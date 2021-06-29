package Caching;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.sach.DemoHib.Relation.Student;

public class App {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		/*
//		Employee emp1 = null;
		Employee emp = new Employee();
		emp.setEid(101); // Put value in database
		emp.setName("Sachin"); // Put value in database
		emp.setDep("IT"); // Put value in database */
		
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

		// Ist level cache is by default provided by Hibernate
		// Ist level cache don't work in different session , for that 2nd level caching needed to be
		// implemented 
		
		System.out.println("Session 1");
		Session s1 = sf.openSession();
		s1.beginTransaction();
		System.out.println(s1.get(Employee.class, 9));	
		System.out.println(s1.get(Employee.class, 9));	

		
		
	/*	//HQL and SQL
		System.out.println("Custom query result HQL");
		Query q = s1.createQuery("select eid,name,dep from Employee where eid>3");
		List<Object[]> empl =  (List<Object[]>) q.list();
		
		for(Object[] employee : empl)
		System.out.println(employee[0]+" ----   "+employee[1]+"  ------- "+employee[2]);

		
		System.out.println("SQL Query");
		SQLQuery q2 = s1.createSQLQuery("select * from Employee");
		q2.addEntity(Employee.class);
		@SuppressWarnings("unchecked")
		List<Employee> epm = q2.list();
		
		for(Employee e : epm)
		System.out.println(e);
		
		
		
		s1.getTransaction().commit();
		s1.close();

		
		System.out.println("Session 2");
		
		
		Session s2 = sf.openSession();
		s2.beginTransaction();
		System.out.println(s2.get(Employee.class, 9));
		s2.getTransaction().commit();
		s2.close();
		
		*/
		
		// Can make normal SQL query or HQL query
	}
}