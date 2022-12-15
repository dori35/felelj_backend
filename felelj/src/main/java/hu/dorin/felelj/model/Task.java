package hu.dorin.felelj.model;

import java.time.Instant;
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

import hu.dorin.felelj.enums.Type;
import lombok.Data;

@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Task{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String text;
	private Type taskType ;
	private Integer timeFrame;
	private Integer point;
	private String solution;
	
	@CreatedDate
	private Instant createdDate;
	
	@ManyToOne
	private Test test;

	@OneToMany(mappedBy = "task")
	private List<Answer> answers;
	
	@OneToMany(mappedBy = "task")
	private List<Choice> choices;

	public Task() {
	}

	public Task(String text,Type taskType ,Integer timeFrame,Integer point, String solution) {
		this.text =text;
		this.taskType = taskType;
		this.timeFrame = timeFrame;
		this.point = point;
		this.solution = solution;
	}
}