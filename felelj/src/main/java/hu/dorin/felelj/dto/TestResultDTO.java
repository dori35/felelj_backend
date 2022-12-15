package hu.dorin.felelj.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDTO {
	
	private Long testId;
	private String fillDate;
	private String title;
	private String subject;
	private Integer timeFrame = 0;
	private Integer taskNumber = 0;
	private Integer maxPoint = 0;
	
	private Integer averagePoint = 0;
	private Integer bestPoint = 0;
	private Integer leastPoint = 0;
	private Integer fillersNumber = 0;
	
	private List<UserResultDTO> fillers;


}
