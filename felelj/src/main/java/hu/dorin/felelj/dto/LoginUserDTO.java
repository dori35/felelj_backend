package hu.dorin.felelj.dto;

import java.util.ArrayList;
import java.util.List;

import hu.dorin.felelj.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginUserDTO {
	private Long id;
	private String identifier;
	private List<Role> roles;
}
