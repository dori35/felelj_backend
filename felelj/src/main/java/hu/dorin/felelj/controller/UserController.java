package hu.dorin.felelj.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.LoginUserDTO;
import hu.dorin.felelj.dto.UserDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.UserRepository;
import hu.dorin.felelj.security.JwtResponse;
import hu.dorin.felelj.security.LoginResponse;
import hu.dorin.felelj.security.SignUpRequest;


@RestController
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	 @Autowired
	 private PasswordEncoder passwordEncoder;
	 
	@GetMapping("/userdtos/{id}")
	public UserDTO getUser(@PathVariable("id") Long id) {
		Optional<User> user = userRepository.findById(id);
		List<Role> rolesList = new ArrayList<Role>();
		
		if(!user.isPresent())
		{
			return null;
		}
		
		
		if(user.get().getRole()== Role.TEACHER ) {
			rolesList.add(Role.TEACHER);
			rolesList.add(Role.STUDENT);
		}else{ 
			rolesList.add(Role.STUDENT);
		}
		
		UserDTO userdto = modelMapper.map(user.get(), UserDTO.class);
		return userdto;
	}
	

	
	@GetMapping("/login/users/{identifier}")
	public LoginUserDTO processLoginUser(@PathVariable("identifier") String identifier) {
		User u = userRepository.findByIdentifier(identifier);
	
		List<Role> rolesList = new ArrayList<Role>();
		if(u.getRole()== Role.TEACHER ) {
			rolesList.add(Role.TEACHER);
			rolesList.add(Role.STUDENT);
		}else{ 
			rolesList.add(Role.STUDENT);
		}
		
		LoginUserDTO loginUserDTO = new LoginUserDTO(u.getId(),u.getIdentifier(), rolesList );
		return loginUserDTO;
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> processRegisterUser(@RequestBody SignUpRequest request) {

		if(request.getName().isEmpty())
		{
			return ResponseEntity.ok(new LoginResponse("empty name"));
		}
		
		if( request.getPassword().length()<4) {
			return ResponseEntity.ok(new LoginResponse("short password"));
		}else if(!request.getPassword().equals(request.getPassword().toLowerCase())) {
			return ResponseEntity.ok(new LoginResponse("password must contain lowercase letter"));
		} 
		else if(!request.getPassword().equals(request.getPassword().toUpperCase()) ) {
			return ResponseEntity.ok(new LoginResponse("password must contain uppercase letter"));
		}
		else if(request.getPassword().matches(".*\\d.*")){
			return ResponseEntity.ok(new LoginResponse("password must contain numbers"));
		}
		
		if(request.getIdentifier().length() != 6)
		{
			return ResponseEntity.ok(new LoginResponse("identifier must be 6 character"));
		}
		
		
        //emailnek ->annotáció?  @Email(regex = "\W*((?i)@companyname.com(?-i))") 
		//role -> eleg?
		
		User user = new User(request.getName(), passwordEncoder.encode( request.getPassword()), request.getEmail(), request.getIdentifier(),Role.valueOf( request.getRole())) ;
		userRepository.save(user);
		return ResponseEntity.ok(new LoginResponse("success registration"));
		
	}
}
