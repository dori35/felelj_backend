package hu.dorin.felelj.request;

import java.util.List;

import hu.dorin.felelj.dto.TaskDTO;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class TestRequest {
	private String title;
	private String subject;
	private Boolean random;
	private List<TaskDTO> tasks;
}
