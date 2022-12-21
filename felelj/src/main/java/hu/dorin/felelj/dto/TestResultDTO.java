package hu.dorin.felelj.dto;

import java.util.List;

import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDTO {
	
	private String startDate;
	private Long testId;
	private String title;
	private String subject;
	private Boolean random;
	private Integer timeFrame = 0;
	private Integer taskNumber = 0;
	private Integer maxPoint = 0;
	
	private double averagePoint = 0;
	private Integer bestPoint = 0;
	private Integer leastPoint = 0;
	private Integer fillersNumber = 0;
	
	private List<UserResultDTO> fillers;
	
	public TestResultDTO( String startDate,
			double averagePoint,Integer bestPoint ,Integer leastPoint, Integer fillersNumber ,
			Long testId, String title,String subject, Boolean random,Integer timeFrame, Integer maxPoint,Integer taskNumber , List<UserResultDTO> fillers)
	{
		this.startDate = startDate;
		this.averagePoint = averagePoint;
		this.bestPoint = bestPoint;
		this.leastPoint = leastPoint;
		this.fillersNumber = fillersNumber;
		this.testId =testId;
		this.title=title;
		this.subject=subject;
		this.random=random;
		this.timeFrame =timeFrame;
		this.taskNumber =taskNumber;
		this.maxPoint =maxPoint;
		this.fillers= fillers;
	}


}
