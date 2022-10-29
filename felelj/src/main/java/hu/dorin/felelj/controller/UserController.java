package hu.dorin.felelj.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.TestDTO;
import hu.dorin.felelj.dto.UserDTO;
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
	
	@GetMapping("/userdtos")
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

	@PostMapping("/signup")
	public String registerUser(@RequestBody SignUpRequest request) {

		User user = modelMapper.map(request,User.class);
		userRepository.save(user);
		return "ok";
	}
}
