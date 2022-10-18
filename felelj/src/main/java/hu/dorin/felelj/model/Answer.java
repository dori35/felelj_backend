package hu.dorin.felelj.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

@Data
@Entity
public class Answer{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String answer;
	
	@CreatedDate
	private Instant createdDate;
	
	@LastModifiedDate
	private Instant LastModifiedDate;
	
	@ManyToOne
	private Task task;
	
	@ManyToOne
	private TestFill testfill;

	public Answer() {
	}
	
	public Answer(String answer) {
		this.answer= answer;
	}

}