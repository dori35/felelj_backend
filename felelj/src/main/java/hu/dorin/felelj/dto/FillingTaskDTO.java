package hu.dorin.felelj.dto;

import java.util.List;

import hu.dorin.felelj.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FillingTaskDTO {
	
	private Long id;
	private String text;
	private Type answerType ;
	private Integer timeFrame;
	private Integer point;
	private List<ChoiceDTO> choices;

}
