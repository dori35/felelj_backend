package hu.dorin.felelj.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResultDTO {

	private Long userId;
	private String identifier;
	private Integer points;
	private List<CompletedTaskDTO> tasks;

}
