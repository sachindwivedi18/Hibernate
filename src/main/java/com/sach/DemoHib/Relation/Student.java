package com.sach.DemoHib.Relation;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Student {
	@Id
	private int rollNo;
	private String Lname;
	private String name;
	@OneToOne
	private Laptop lap;
	
	@Override
	public String toString() {
		return "Student [rollNo=" + rollNo + ", Lname=" + Lname + ", name=" + name + ", lap=" + lap + "]";
	}
} 