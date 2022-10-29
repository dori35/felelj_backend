package hu.dorin.felelj.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class TestFill{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Date fillDate;
	private Integer point;

	@ManyToOne
	private Test test;
	
	@ManyToOne
	private User user;
	
	@OneToMany(mappedBy = "testFill")
	private List<Answer> answers;
	
	public TestFill() {
	}

	

}