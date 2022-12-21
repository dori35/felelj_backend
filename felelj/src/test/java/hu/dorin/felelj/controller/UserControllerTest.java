package hu.dorin.felelj.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import hu.dorin.felelj.dto.UserDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.UserRepository;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class UserControllerTest {

	@LocalServerPort
    private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@MockBean
	private UserRepository userRepository;
	
	@Test
	void testGetUser() {
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"zi2xg3"	,Role.STUDENT);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user) );
	
		UserDTO userDto = this.restTemplate.getForObject("http://localhost:" + port + "/userdtos/1",UserDTO.class);
		assertEquals(userDto.getName(), "Imelda Jacobs");
		assertEquals(userDto.getEmail(), "gerry.hudson@gmail.com");
		assertEquals(userDto.getIdentifier(), "zi2xg3");
		assertEquals(userDto.getRole(), Role.STUDENT);
	}

}
