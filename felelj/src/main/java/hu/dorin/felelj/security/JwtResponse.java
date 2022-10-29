package hu.dorin.felelj.security;

import java.util.List;

import lombok.Data;

@Data
public class JwtResponse {
	private Long id;
	private String token;
	private String type = "Bearer";
	private String identifier;
	private List<String> roles;

	public JwtResponse(Long id,String accessToken, String username, List<String> roles) {
		this.id = id;
		this.token = accessToken;
		this.identifier = username;
		this.roles = roles;
	}
	
}
