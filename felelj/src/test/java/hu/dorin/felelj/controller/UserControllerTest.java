package hu.dorin.felelj.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import hu.dorin.felelj.dto.LoginUserDTO;
import hu.dorin.felelj.dto.UserDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.UserRepository;
import net.minidev.json.JSONObject;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

	@LocalServerPort
    private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@MockBean
	private UserRepository userRepository;
	
	
	@Test
	void testGetUser() {
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"user1"	,Role.STUDENT);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user) );
	
		UserDTO userDto = this.restTemplate.getForObject("http://localhost:" + port + "/profile/1",UserDTO.class);
		assertEquals( "Imelda Jacobs",userDto.getName());
		assertEquals("gerry.hudson@gmail.com",userDto.getEmail());
		assertEquals( "user1",userDto.getIdentifier());
		assertEquals( Role.STUDENT,userDto.getRole());
	}
	
	@Test
	void testGetUserInvalidUser() {
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"user1"	,Role.STUDENT);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user) );
	
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","user not found" );
		JSONObject json = this.restTemplate.getForObject("http://localhost:" + port + "/profile/2",JSONObject.class);
		assertEquals(jsonObj, json);
	}
	
	
	@Test
	void testGetLoginUser() {
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"user1"	,Role.STUDENT);
		user.setId(1L);
		when(userRepository.findByIdentifier("user1")).thenReturn(Optional.of(user) );
		
		List<Role> rolesList = new ArrayList<Role>();
		rolesList.add(Role.STUDENT);
	
		LoginUserDTO loginUserDTO = this.restTemplate.getForObject("http://localhost:" + port + "/login/users/user1",LoginUserDTO.class);
		assertEquals( "user1",loginUserDTO.getIdentifier());
		assertEquals(1L,loginUserDTO.getId());
		assertEquals( rolesList,loginUserDTO.getRoles());
	}
	
	@Test
	void testGetLoginUserInvalidUser() {
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"user1"	,Role.STUDENT);
		when(userRepository.findByIdentifier("user1")).thenReturn(Optional.of(user) );
	
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","user not found" );
		JSONObject json = this.restTemplate.getForObject("http://localhost:" + port + "/login/users/user2",JSONObject.class);
		assertEquals(jsonObj, json);

	}
	

}
