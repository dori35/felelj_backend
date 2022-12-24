package hu.dorin.felelj.dto;

import hu.dorin.felelj.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
	private String name;
	private String email;
	private String identifier;
	private Role role;
}
