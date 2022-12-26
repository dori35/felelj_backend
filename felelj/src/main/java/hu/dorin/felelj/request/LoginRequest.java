package hu.dorin.felelj.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
	private String identifier;
	private String password;
}

