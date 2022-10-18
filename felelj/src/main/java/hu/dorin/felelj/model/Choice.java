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

import lombok.Data;

@Data
@Entity
public class Choice{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String text;
	
	@CreatedDate
	private Instant createdDate;
	
	@LastModifiedDate
	private Instant LastModifiedDate;
	
	@ManyToOne
	private Task task;
	
	public Choice() {
	}

	public Choice(String text) {
		this.text =text;
	}


}