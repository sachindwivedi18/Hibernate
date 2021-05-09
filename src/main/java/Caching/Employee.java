package Caching;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity//(name="Emp")
@Cacheable
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class Employee {
	
	@Id
	private int eid;
//	@Column(name="firstName")
	String  name;
	private String dep;
	@Override
	public String toString() {
		return "Employee [eid=" + eid + ", name=" + name + ", dep=" + dep + "]";
	}
	
	
}