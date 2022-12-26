package hu.dorin.felelj.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.LoginUserDTO;
import hu.dorin.felelj.dto.UserDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.UserRepository;
import net.minidev.json.JSONObject;


@RestController
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	@GetMapping("/profile/{id}")
	public ResponseEntity<?> getUser(@PathVariable("id") Long id) {
		Optional<User> userOpt = userRepository.findById(id);
		List<Role> rolesList = new ArrayList<Role>();
		JSONObject jsonObj = new JSONObject();
		
		if(!userOpt.isPresent())
		{
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		User user = userOpt.get();
		
		if(user.getRole()== Role.TEACHER ) {
			rolesList.add(Role.TEACHER);
			rolesList.add(Role.STUDENT);
		}else if(user.getRole()== Role.STUDENT ){ 
			rolesList.add(Role.STUDENT);
		}else {
			jsonObj.put("error","invalid role" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		UserDTO userdto = modelMapper.map(user, UserDTO.class);
		return new ResponseEntity<>(userdto,HttpStatus.OK);
	}
	

	
	@GetMapping("/login/users/{identifier}")
	public ResponseEntity<?> getLoginUser(@PathVariable("identifier") String identifier) {
		Optional<User> userOpt = userRepository.findByIdentifier(identifier);
		List<Role> rolesList = new ArrayList<Role>();
		JSONObject jsonObj = new JSONObject();
		
		if(!userOpt.isPresent())
		{
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		User user = userOpt.get();
		
		
		if(user.getRole()== Role.TEACHER ) {
			rolesList.add(Role.TEACHER);
			rolesList.add(Role.STUDENT);
		}else if(user.getRole()== Role.STUDENT ){ 
			rolesList.add(Role.STUDENT);
		}else {
			jsonObj.put("error","invalid role" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		LoginUserDTO loginUserDTO = new LoginUserDTO(user.getId(),user.getIdentifier(),rolesList);
		return new ResponseEntity<>(loginUserDTO,HttpStatus.OK);
	}
	
	
}
