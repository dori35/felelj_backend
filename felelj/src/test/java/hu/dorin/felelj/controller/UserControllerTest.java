package hu.dorin.felelj.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import hu.dorin.felelj.dto.UserDTO;
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
		
		UserDTO userDto = this.restTemplate.getForObject("http://localhost:" + port + "/userdtos/1",UserDTO.class);
		assertEquals(userDto, "11.99 K");
	}

}
