package hu.dorin.felelj.security;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
	private String identifier;
	private String password;
}
