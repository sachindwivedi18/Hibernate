package com.sach.DemoHib.Relation;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Laptop {
	
	@Id
		private int Lid;
		private String Lname;
		@Override
		public String toString() {
			return "Laptop [Lid=" + Lid + ", Lname=" + Lname + "]";
		}
}