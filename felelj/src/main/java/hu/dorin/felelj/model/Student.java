package hu.dorin.felelj.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;


@Data
@Entity
public class Student{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String password;
	private String email;
	private String identifier;

	
	public Student() {
	}

	public Student(String name, String password, String email, String identifier) {
	        this.name = name;
	        this.password = password;
	        this.email = email;
	        this.identifier = identifier;
	        
	  }

}
