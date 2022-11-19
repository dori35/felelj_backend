package hu.dorin.felelj.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedTestDTO {
	
	private Long id;
	private String title;
	private String subject;
	private Date time;
	private Integer currentPoint = 5;
	private Integer maxPoint = 20;
	private Integer taskNumber = 10;

}
