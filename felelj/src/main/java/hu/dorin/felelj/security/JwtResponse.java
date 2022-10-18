package hu.dorin.felelj.security;

import java.util.List;

import lombok.Data;

@Data
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String username;
	private List<String> roles;

	public JwtResponse(String accessToken, String username, List<String> roles) {
		this.token = accessToken;
		this.username = username;
		this.roles = roles;
	}
	
}
