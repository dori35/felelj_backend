package hu.dorin.felelj.dto;

import java.util.List;


import hu.dorin.felelj.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
	
	private Long id;
	private String text;
	private Type taskType ;
	private Integer timeFrame;
	private Integer point;
	private String solution;
	private List<ChoiceDTO> choices;

}