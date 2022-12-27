package hu.dorin.felelj.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import hu.dorin.felelj.dto.UserDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.UserRepository;
import hu.dorin.felelj.request.LoginRequest;
import hu.dorin.felelj.request.SignUpRequest;
import hu.dorin.felelj.security.JwtResponse;
import hu.dorin.felelj.security.JwtUtils;
import net.minidev.json.JSONObject;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

	@LocalServerPort
    private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@MockBean
	private UserRepository userRepository;

	@Test
	void testAuthenticateUser() {
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"user1"	,Role.TEACHER);
		user.setId(1L);
		when(userRepository.findByIdentifier("user1")).thenReturn(Optional.of(user) );
	
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setIdentifier("user1");
		loginRequest.setPassword("1234");
		
		List<String> rolesList = new ArrayList<String>();
		rolesList.add("TEACHER");
		rolesList.add("STUDENT");

		JwtResponse jwtResponse = this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signin", loginRequest, JwtResponse.class );
		assertNotNull(jwtResponse);
		assertEquals("user1",jwtResponse.getIdentifier());
		assertEquals(rolesList,jwtResponse.getRoles());
		assertEquals(1L,jwtResponse.getId());
	}
	
	
	@Test
	void testAuthenticateUserInvalidUser() {
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"user1"	,Role.TEACHER);
		user.setId(1L);
		when(userRepository.findByIdentifier("user1")).thenReturn(Optional.of(user) );
	
		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setIdentifier("user5");
		
		try {
			this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signin", loginRequest,Object.class);
		   }
		   catch (AuthenticationException e) {
			   assertNotNull(e);
		}
		
		LoginRequest loginRequest2 = new LoginRequest();
		loginRequest.setIdentifier("user1");
		loginRequest.setPassword("4321");

		try {
			this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signin", loginRequest2,Object.class);
		   }
		   catch (AuthenticationException e) {
			   assertNotNull(e);
		}
	}
	
	@Test
	void testProcessRegisterUser() {
		SignUpRequest request = new SignUpRequest();
		request.setIdentifier("admin1");
		request.setPassword("Alma1");
		request.setName("Mr User");
		request.setEmail("user1@gmail.com");
		request.setRole("TEACHER");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("text","successful registration" );
		JSONObject json = this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signup", request ,JSONObject.class);
		assertEquals(jsonObj, json);
	
	}
	
	
	@Test
	void testProcessRegisterUserEmptyName() {
		SignUpRequest request = new SignUpRequest();
		request.setIdentifier("admin1");
		request.setPassword("Alma1");
		request.setName("");
		request.setEmail("user1@gmail.com");
		request.setRole("TEACHER");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","empty name" );
		JSONObject json = this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signup", request ,JSONObject.class);
		assertEquals(jsonObj, json);
	
	}
	
	@Test
	void testProcessRegisterUserInvalidPass1() {
		SignUpRequest request = new SignUpRequest();
		request.setIdentifier("admin1");
		request.setPassword("pas");
		request.setName("Mr User");
		request.setEmail("user1@gmail.com");
		request.setRole("TEACHER");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","short password" );
		JSONObject json = this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signup", request ,JSONObject.class);
		assertEquals(jsonObj, json);
	
	}
	

	@Test
	void testProcessRegisterUserInvalidPass2() {
		SignUpRequest request = new SignUpRequest();
		request.setIdentifier("admin1");
		request.setPassword("ALMA1");
		request.setName("Mr User");
		request.setEmail("user1@gmail.com");
		request.setRole("TEACHER");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","password must contain lowercase letter" );
		JSONObject json = this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signup", request ,JSONObject.class);
		assertEquals(jsonObj, json);
	
	}
	
	@Test
	void testProcessRegisterUserInvalidPass3() {
		SignUpRequest request = new SignUpRequest();
		request.setIdentifier("admin1");
		request.setPassword("alma1");
		request.setName("Mr User");
		request.setEmail("user1@gmail.com");
		request.setRole("TEACHER");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","password must contain uppercase letter" );
		JSONObject json = this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signup", request ,JSONObject.class);
		assertEquals(jsonObj, json);
	
	}
	

	@Test
	void testProcessRegisterUserInvalidPass4() {
		SignUpRequest request = new SignUpRequest();
		request.setIdentifier("admin1");
		request.setPassword("aLma");
		request.setName("Mr User");
		request.setEmail("user1@gmail.com");
		request.setRole("TEACHER");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","password must contain numbers" );
		JSONObject json = this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signup", request ,JSONObject.class);
		assertEquals(jsonObj, json);
	
	}
	
	@Test
	void testProcessRegisterUserInvaliIdentifier() {
		SignUpRequest request = new SignUpRequest();
		request.setIdentifier("admin");
		request.setPassword("Alma1");
		request.setName("Mr User");
		request.setEmail("user1@gmail.com");
		request.setRole("TEACHER");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","identifier must be 6 character" );
		JSONObject json = this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signup", request ,JSONObject.class);
		assertEquals(jsonObj, json);
	
	}
	
	@Test
	void testProcessRegisterUserInvalidRole() {
		SignUpRequest request = new SignUpRequest();
		request.setIdentifier("admin1");
		request.setPassword("Alma1");
		request.setName("Mr User");
		request.setEmail("user1@gmail.com");
		request.setRole("role");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","invalid role" );
		JSONObject json = this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signup", request ,JSONObject.class);
		assertEquals(jsonObj, json);
	
	}
	
	
	@Test
	void testProcessRegisterUserInvalidEmail() {
		SignUpRequest request = new SignUpRequest();
		request.setIdentifier("admin1");
		request.setPassword("Alma1");
		request.setName("Mr User");
		request.setEmail("umail.com");
		request.setRole("TEACHER");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","invalid email" );
		JSONObject json = this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signup", request ,JSONObject.class);
		assertEquals(jsonObj, json);
	
	}
	
	@Test
	void testProcessRegisterUserConflict() {
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"admin1"	,Role.TEACHER);
		user.setId(1L);
		when(userRepository.findByIdentifier("admin1")).thenReturn(Optional.of(user) );

		SignUpRequest request = new SignUpRequest();
		request.setIdentifier("admin1");
		request.setPassword("Alma1");
		request.setName("Mr User");
		request.setEmail("user@gmail.com");
		request.setRole("TEACHER");
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","user exists" );
		JSONObject json = this.restTemplate.postForObject("http://localhost:" + port + "/api/auth/signup", request ,JSONObject.class);
		assertEquals(jsonObj, json);
	
	}
	
	
	
	
	
}
