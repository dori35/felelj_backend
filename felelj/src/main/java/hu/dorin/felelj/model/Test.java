package hu.dorin.felelj.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Test {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private String subject;
	private Boolean random;
	private Boolean isActive = true;
	
	@CreatedDate
	private Instant createdDate;
	
	@ManyToOne
	private User createdBy ;

	@ManyToMany(mappedBy = "completedTests")
	private List<User> submitters ;
	
	@OneToMany(mappedBy = "test")
	private List<Task> tasks;
	
	@OneToMany(mappedBy = "test")
	private List<TestFill> testFills;
	
	public Test() {
	}

	public Test(String title,  String subject,Boolean random) {
		this.title = title;
		this.subject = subject;
		this.random = random;
	}
}