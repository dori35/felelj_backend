package hu.dorin.felelj.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import hu.dorin.felelj.enums.Role;
import lombok.Data;

@Data
@Entity
public class User{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String password;
	private String email;
	private String identifier;
	private Role role;

	@OneToMany(mappedBy = "createdBy")
	private List<Test> createdTests;
	
	@OneToMany(mappedBy = "user")
	private List<TestFill> testfills;
	
	@ManyToMany
	@JoinTable(name = "test_class", 
    	joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), 
    	inverseJoinColumns = @JoinColumn(name = "test_id", referencedColumnName = "id"))
	private List<Test> completedTests;
	
	public User() {
	}

	public User(String name, String password, String email, String identifier, Role role) {
	        this.name = name;
	        this.password = password;
	        this.email = email;
	        this.identifier = identifier;
	        this.role = role;
	  }

}

