package Caching;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable   // When this entity is part of another, kind off like association
public class EmployeeName {

	private String f_name;
	private String l_name;
	private String m_name;
	
	@Override
	public String toString() {
		return "EmployeeName [f_name=" + f_name + ", l_name=" + l_name + ", m_name=" + m_name + "]";
	}
}
