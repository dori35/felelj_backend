package hu.dorin.felelj.dto;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
	private Date time;
	private Date startDate;
	private Boolean random;
	private Instant createdDate;
	private Instant lastModifiedDate;
	private Integer point = 0;
	private Integer taskNumber =10;

}
