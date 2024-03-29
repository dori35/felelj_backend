package hu.dorin.felelj.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopResultsDTO {

	private Integer currentPoints=-1;
	private Integer maxPoints;
	private List<TopDTO> topThree;
	
}
