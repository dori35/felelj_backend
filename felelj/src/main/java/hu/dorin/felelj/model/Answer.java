package hu.dorin.felelj.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Answer{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String answer;
	
	@CreatedDate
	private Instant createdDate;
	
	@ManyToOne
	private Task task;
	
	@ManyToOne
	private TestFill testFill;

	public Answer() {
	}
	
	public Answer(String answer) {
		this.answer= answer;
	}

}