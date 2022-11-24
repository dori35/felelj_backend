package hu.dorin.felelj.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.LoginUserDTO;
import hu.dorin.felelj.dto.UserDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.UserRepository;


@RestController
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
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
	public LoginUserDTO getLoginUser(@PathVariable("identifier") String identifier) {
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
	
	
}
