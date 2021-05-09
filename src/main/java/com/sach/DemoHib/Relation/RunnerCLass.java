package com.sach.DemoHib.Relation;

import javax.imageio.spi.ServiceRegistry;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class RunnerCLass {

	public static void main(String[] args) {
		
		Laptop lap =  new Laptop();
		lap.setLid(1);
		lap.setLname("Dell");
		
		Student st = new Student();
		st.setLname("Sach");
		st.setRollNo(101);
		st.setName("Dwd");

		st.setLap(lap);
		
		Configuration config = new Configuration().configure().addAnnotatedClass(Student.class).addAnnotatedClass(Laptop.class);
		SessionFactory sf = config.buildSessionFactory();
		Session ses = sf.openSession();
		
		ses.beginTransaction();
		ses.save(lap);
		ses.save(st);
		ses.getTransaction().commit();
		
	}
}