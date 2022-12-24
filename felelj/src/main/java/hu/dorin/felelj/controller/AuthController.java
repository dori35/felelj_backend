package hu.dorin.felelj.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import net.minidev.json.JSONObject;


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
		
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		Optional<User> userOpt = userRepository.findByIdentifier(userDetails.getUsername());
		JSONObject jsonObj = new JSONObject();
		if(!userOpt.isPresent())
		{
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.UNAUTHORIZED);
		}
		
		User user = userOpt.get();
		
		return ResponseEntity.ok(new JwtResponse(user.getId(),jwt,userDetails.getUsername(),roles));
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> processRegisterUser(@RequestBody SignUpRequest request) {
		JSONObject jsonObj = new JSONObject();
		if(request.getName().isEmpty() || request.getName().isBlank())
		{
			jsonObj.put("error","empty name" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
		
		if( request.getPassword().length()<4) {
			jsonObj.put("error","short password" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}else if(request.getPassword().equals(request.getPassword().toUpperCase())) {
			jsonObj.put("error","password must contain lowercase letter" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		} 
		else if(request.getPassword().equals(request.getPassword().toLowerCase()) ) {
			jsonObj.put("error","password must contain uppercase letter" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
		else if(!request.getPassword().matches(".*\\d.*")){
			jsonObj.put("error","password must contain numbers" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
		
		if(request.getIdentifier().trim().length() != 6)
		{
			jsonObj.put("error","identifier must be 6 character" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
		
		if( !(request.getRole().equals(Role.STUDENT.toString()) || request.getRole().equals( Role.TEACHER.toString())) )
		{
			jsonObj.put("error","invalid role" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
		
		if(!request.getEmail().matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,6}$"))
		{
			jsonObj.put("error","invalid email" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
	
		
		Optional<User> userOpt = userRepository.findByIdentifier(request.getIdentifier());
		if(userOpt.isPresent())
		{
			jsonObj.put("error","user exists" );
			return new ResponseEntity<>(jsonObj,HttpStatus.CONFLICT);
		}
		
		User user = new User(request.getName(), passwordEncoder.encode( request.getPassword()), request.getEmail(), request.getIdentifier(),Role.valueOf( request.getRole())) ;
		userRepository.save(user);
		jsonObj.put("text","successful registration" );
		return new ResponseEntity<>(jsonObj,HttpStatus.OK);
		
	}
	
	
	
}