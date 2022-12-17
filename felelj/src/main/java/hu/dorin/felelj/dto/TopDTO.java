package hu.dorin.felelj.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopDTO {
	
	private Integer points;
	private List<String> identifiers;
}
