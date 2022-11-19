package hu.dorin.felelj.dto;

import java.time.Instant;
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
	private Date time;
	private Boolean random;
	private Integer point = 0;
	private Integer taskNumber =10;
	private List<FillingTaskDTO> tasks;

}
