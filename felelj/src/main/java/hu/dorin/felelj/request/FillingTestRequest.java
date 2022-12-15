package hu.dorin.felelj.request;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FillingTestRequest {
	private List<AnswerRequest> answers = new ArrayList<AnswerRequest>();
	private String startDate;
}
