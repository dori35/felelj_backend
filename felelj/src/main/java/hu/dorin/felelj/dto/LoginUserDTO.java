package hu.dorin.felelj.dto;

import java.util.List;

import hu.dorin.felelj.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDTO {
	private Long id;
	private String identifier;
	private List<Role> roles;
}
