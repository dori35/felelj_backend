package hu.dorin.felelj.security;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpRequest {
	private String identifier;
	private String password;
	private String  name;
    private String email;
    private String role;
}
