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
import hu.dorin.felelj.dto.TestDTO;
import hu.dorin.felelj.dto.UserDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.UserRepository;
import hu.dorin.felelj.security.SignUpRequest;


@RestController
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	 @Autowired
	 private PasswordEncoder passwordEncoder;
	 
	/*@GetMapping("/userdtos")
	public List<UserDTO> getUsers() {
		Iterable<User> users = userRepository.findAll();
		List<UserDTO> userDtosList = new ArrayList<>();
		for (User u : users) {
			List<TestDTO> testDtoList = new ArrayList<>();
			for (Test t : u.getCreatedTests()) {
				TestDTO tdto = modelMapper.map(t, TestDTO.class);
				testDtoList.add(tdto);
			}
			UserDTO dto = new UserDTO(u.getId(), u.getName(),u.getPassword(), u.getEmail(), u.getIdentifier(),u.getRole(),testDtoList);
			userDtosList.add(dto);
		}
		
		return userDtosList;
	}*/
	
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
	

	@GetMapping("/testdtos/{id}")
	public List<TestDTO> getTests(@PathVariable("id") String id) {
		Optional<User> user = userRepository.findById(Long.parseLong(id));
		List<TestDTO> testDtoList = new ArrayList<>();
		if(!user.isPresent())
		{
			return testDtoList;
		}
		
		for (Test t : user.get().getCreatedTests()) {
				TestDTO tdto = modelMapper.map(t, TestDTO.class);
				testDtoList.add(tdto);
		}
		return testDtoList;
	}
	
	@GetMapping("/completedtestdtos/{id}")
	public List<TestDTO> getCompletedTests(@PathVariable("id") String id) {
		Optional<User> user = userRepository.findById(Long.parseLong(id));
		List<TestDTO> testDtoList = new ArrayList<>();
		if(!user.isPresent())
		{
			return testDtoList;
		}
		
		for (Test t : user.get().getCreatedTests()) {
				TestDTO tdto = modelMapper.map(t, TestDTO.class);
				testDtoList.add(tdto);
		}
		return testDtoList;
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
	public String processRegisterUser(@RequestBody SignUpRequest request) {

		User user = new User(request.getName(), passwordEncoder.encode( request.getPassword()), request.getEmail(), request.getIdentifier(),Role.valueOf( request.getRole())) ;
		userRepository.save(user);
		return "success registration";
	}
}
