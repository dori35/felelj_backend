package hu.dorin.felelj.dto;

import java.util.List;

import hu.dorin.felelj.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
	
	private Long id;
	private String name;
	private String password;
	private String email;
	private String identifier;
	private Role role;
	private List<TestDTO> tests;
}
