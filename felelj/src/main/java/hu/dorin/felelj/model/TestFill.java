package hu.dorin.felelj.model;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class TestFill{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreatedDate
	private Instant fillDate;
	private Integer point;
	private String startDate;

	@ManyToOne
	private Test test;
	
	@ManyToOne
	private User user;
	
	@OneToMany(mappedBy = "testFill")
	private List<Answer> answers;
	
	public TestFill() {
	}
	
	public TestFill(Test test,User user,String startDate) {
		this.test = test;
		this.user = user;
		this.startDate = startDate;
	}
		

}