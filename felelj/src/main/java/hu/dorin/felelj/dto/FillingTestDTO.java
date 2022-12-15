package hu.dorin.felelj.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FillingTestDTO {
	
	private Long id;
	private String title;
	private String subject;
	private Boolean random;
	private Integer time = 0;
	private Integer point = 0;
	private Integer taskNumber = 0;
	private String  startDate = "";
	private List<FillingTaskDTO> tasks;

}
