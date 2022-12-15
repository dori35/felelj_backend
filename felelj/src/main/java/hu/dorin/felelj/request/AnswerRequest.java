package hu.dorin.felelj.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnswerRequest {
	private Long id;
	private String answer ;
}
