package hu.dorin.felelj.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StartTestRequest{
	private String url;
	private String startTime;
}
