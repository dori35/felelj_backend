package hu.dorin.felelj.model;

import java.time.Instant;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import hu.dorin.felelj.enums.Type;
import lombok.Data;

@Data
@Entity
public class Task{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String text;
	private Type answerType ;
	private int timeFrame;
	private int point;
	private String solution;
	
	@CreatedDate
	private Instant createdDate;
	
	@LastModifiedDate
	private Instant LastModifiedDate;
	
	@ManyToOne
	private Test test;

	@OneToMany(mappedBy = "task")
	private List<Answer> answers;
	
	@OneToMany(mappedBy = "task")
	private List<Choice> choices;

	public Task() {
	}

	public Task(String text,Type answerType ,int timeFrame,int point, String solution) {
		this.text =text;
		this.answerType = answerType;
		this.timeFrame = timeFrame;
		this.point = point;
		this.solution = solution;
	}
}