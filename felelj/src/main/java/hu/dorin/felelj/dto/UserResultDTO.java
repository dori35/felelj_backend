package hu.dorin.felelj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResultDTO {
	private String identifiers;
	private Integer currentPoint;

}
