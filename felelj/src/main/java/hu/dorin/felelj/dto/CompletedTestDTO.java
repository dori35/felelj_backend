package hu.dorin.felelj.dto;

import java.util.Date;
import java.util.List;

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
	private String fillDate;
	private Integer currentPoint;
	private Integer timeFrame = 0;
	private Integer taskNumber = 0;
	private Integer maxPoint = 0;
	private List<CompletedTaskDTO> tasks;

}
