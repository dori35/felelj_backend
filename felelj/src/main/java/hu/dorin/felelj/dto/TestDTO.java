package hu.dorin.felelj.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDTO {
	
	private Long id;
	private String title;
	private String subject;
	private Boolean random;
	private String createdDate;
	private Integer time = 0;
	private Integer point = 0;
	private Integer taskNumber = 0;
	private List<TaskDTO> tasks;

}
