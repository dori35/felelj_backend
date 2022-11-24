package hu.dorin.felelj.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.UserRepository;
import hu.dorin.felelj.security.JwtResponse;
import hu.dorin.felelj.security.JwtUtils;
import hu.dorin.felelj.security.LoginRequest;
import hu.dorin.felelj.security.LoginResponse;
import hu.dorin.felelj.security.SignUpRequest;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	 
	@Autowired
	private JwtUtils jwtUtils;
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword()));
		
		//SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());
		
		
		return ResponseEntity.ok(new JwtResponse(userRepository.findByIdentifier(userDetails.getUsername()).getId(),jwt, 
												 userDetails.getUsername(), 
												 roles));
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> processRegisterUser(@RequestBody SignUpRequest request) {
		

		if(request.getName().isEmpty())
		{
			return ResponseEntity.ok(new LoginResponse("empty name"));
		}
		
		if( request.getPassword().length()<4) {
			return ResponseEntity.ok(new LoginResponse("short password"));
		}else if(request.getPassword().equals(request.getPassword().toUpperCase())) {
			return ResponseEntity.ok(new LoginResponse("password must contain lowercase letter"));
		} 
		else if(request.getPassword().equals(request.getPassword().toLowerCase()) ) {
			return ResponseEntity.ok(new LoginResponse("password must contain uppercase letter"));
		}
		else if(!request.getPassword().matches(".*\\d.*")){
			return ResponseEntity.ok(new LoginResponse("password must contain numbers"));
		}
		
		if(request.getIdentifier().length() != 6)
		{
			return ResponseEntity.ok(new LoginResponse("identifier must be 6 character"));
		}
		
		if( !(request.getRole().equals(Role.STUDENT.toString()) || request.getRole().equals( Role.TEACHER.toString())) )
		{
			return ResponseEntity.ok(new LoginResponse("invalid role"));
		}
		
		if(!request.getEmail().matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,6}$"))
		{
			return ResponseEntity.ok(new LoginResponse("invalid email"));
		}
	
		User user = new User(request.getName(), passwordEncoder.encode( request.getPassword()), request.getEmail(), request.getIdentifier(),Role.valueOf( request.getRole())) ;
		userRepository.save(user);
		return ResponseEntity.ok(new LoginResponse("success registration"));
		
	}
	
	
	
}