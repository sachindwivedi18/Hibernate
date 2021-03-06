package com.sach.DemoHibBasics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity//(name="Emp")
public class Employee {
	@Id
	private int eid;
	private EmployeeName  name;
	private String dep;
	@Override
	public String toString() {
		return "Employee [eid=" + eid + ", name=" + name + ", dep=" + dep + "]";
	}
}