package hu.dorin.felelj.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import hu.dorin.felelj.dto.CompletedTaskDTO;
import hu.dorin.felelj.dto.CompletedTestDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.enums.Type;
import hu.dorin.felelj.model.Answer;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.TestFill;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.TestFillRepository;
import hu.dorin.felelj.repository.UserRepository;
import hu.dorin.felelj.request.SignUpRequest;
import net.minidev.json.JSONObject;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CompletedTestControllerTest {
	
	@LocalServerPort
    private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private TestFillRepository testFillRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Test
	void testGetCompletedTests() {
		
		List<TestFill> testFillList  = new ArrayList<TestFill>();
		List<CompletedTestDTO> dtoList = new ArrayList<CompletedTestDTO>();
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"user5"	,Role.STUDENT);
		user.setId(5L);
		
		when(userRepository.findByIdentifier("user5")).thenReturn(Optional.of(user) );
		when(userRepository.findById(5L)).thenReturn(Optional.of(user) );
		when(testFillRepository.findByUser(user)).thenReturn(testFillList);

		CompletedTestDTO[] res = this.restTemplate.getForObject("http://localhost:" + port + "/completedtest/5", CompletedTestDTO[].class);
		assertNotNull(res);
		assertTrue(res.length==0);
	}
	
	@Test
	void testGetCompletedTestsZeroPoint() {

		CompletedTaskDTO taskDTO = new CompletedTaskDTO();
		taskDTO.setId(1L);
		taskDTO.setPoint(5);
		taskDTO.setTaskType(Type.TRUE_FALSE);
		taskDTO.setSolution("1");
		taskDTO.setAnswer("0");
		taskDTO.setCurrentPoint(5);
		taskDTO.setText("text1");
		taskDTO.setTimeFrame(5);
		taskDTO.setChoices(null);
		
		CompletedTaskDTO taskDTO2 = new CompletedTaskDTO();
		taskDTO2.setId(2L);
		taskDTO2.setPoint(5);
		taskDTO2.setTaskType(Type.TRUE_FALSE);
		taskDTO2.setSolution("0");
		taskDTO2.setAnswer("1");
		taskDTO2.setCurrentPoint(0);
		taskDTO2.setText("text2");
		taskDTO2.setTimeFrame(15);
		taskDTO2.setChoices(null);
		

		
		Task task1  = modelMapper.map(taskDTO, Task.class);
		Task task2  = modelMapper.map(taskDTO2, Task.class);
		List<Task> taskList = new ArrayList<Task>();
		taskList.add(task1);
		taskList.add(task2);
		hu.dorin.felelj.model.Test test  = new hu.dorin.felelj.model.Test();
		test.setTasks(taskList);
		List<TestFill> testFillList  = new ArrayList<TestFill>();
		TestFill testFill = new TestFill();
		testFill.setId(1L);
		

		List<Answer> answerList = new ArrayList<Answer>();
		
		Answer answer = new Answer();
		answer.setAnswer("0");
		Answer answer2 = new Answer();
		answer2.setAnswer("1");
		testFill.setFillDate( Instant.parse("2022-10-03T11:25:30.00Z"));
		testFill.setPoint(0);
		testFill.setTest(test);
		testFill.setAnswers(answerList);
		testFill.setStartDate("2022/9/4 9:59:40");
		testFillList.add(testFill);
		List<CompletedTestDTO> dtoList = new ArrayList<CompletedTestDTO>();
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"user5"	,Role.STUDENT);
		user.setId(5L);
		
		when(userRepository.findByIdentifier("user5")).thenReturn(Optional.of(user) );
		when(userRepository.findById(5L)).thenReturn(Optional.of(user) );
		when(testFillRepository.findByUser(user)).thenReturn(testFillList);

		CompletedTestDTO[] res = this.restTemplate.getForObject("http://localhost:" + port + "/completedtest/5", CompletedTestDTO[].class);
		assertNotNull(res);
		assertTrue(res.length==1);
		assertTrue(res[0].getMaxPoint()==10);
		assertTrue(res[0].getCurrentPoint()==0);
	}
	
	@Test
	void testGetCompletedTestsMaxPoint() {

		CompletedTaskDTO taskDTO = new CompletedTaskDTO();
		taskDTO.setId(1L);
		taskDTO.setPoint(5);
		taskDTO.setTaskType(Type.TRUE_FALSE);
		taskDTO.setSolution("0");
		taskDTO.setAnswer("0");
		taskDTO.setCurrentPoint(5);
		taskDTO.setText("text1");
		taskDTO.setTimeFrame(5);
		taskDTO.setChoices(null);
		
		CompletedTaskDTO taskDTO2 = new CompletedTaskDTO();
		taskDTO2.setId(2L);
		taskDTO2.setPoint(5);
		taskDTO2.setTaskType(Type.TRUE_FALSE);
		taskDTO2.setSolution("1");
		taskDTO2.setAnswer("1");
		taskDTO2.setCurrentPoint(0);
		taskDTO2.setText("text2");
		taskDTO2.setTimeFrame(15);
		taskDTO2.setChoices(null);
		

		
		Task task1  = modelMapper.map(taskDTO, Task.class);
		Task task2  = modelMapper.map(taskDTO2, Task.class);
		List<Task> taskList = new ArrayList<Task>();
		taskList.add(task1);
		taskList.add(task2);
		hu.dorin.felelj.model.Test test  = new hu.dorin.felelj.model.Test();
		test.setTasks(taskList);
		List<TestFill> testFillList  = new ArrayList<TestFill>();
		TestFill testFill = new TestFill();
		testFill.setId(1L);
		

		List<Answer> answerList = new ArrayList<Answer>();
		
		Answer answer = new Answer();
		answer.setAnswer("0");
		Answer answer2 = new Answer();
		answer2.setAnswer("1");
		testFill.setFillDate( Instant.parse("2022-10-03T11:25:30.00Z"));
		testFill.setPoint(10);
		testFill.setTest(test);
		testFill.setAnswers(answerList);
		testFill.setStartDate("2022/9/4 9:59:40");
		testFillList.add(testFill);
		List<CompletedTestDTO> dtoList = new ArrayList<CompletedTestDTO>();
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"user5"	,Role.STUDENT);
		user.setId(5L);
		
		when(userRepository.findByIdentifier("user5")).thenReturn(Optional.of(user) );
		when(userRepository.findById(5L)).thenReturn(Optional.of(user) );
		when(testFillRepository.findByUser(user)).thenReturn(testFillList);

		CompletedTestDTO[] res = this.restTemplate.getForObject("http://localhost:" + port + "/completedtest/5", CompletedTestDTO[].class);
		assertNotNull(res);
		assertTrue(res.length==1);
		assertTrue(res[0].getMaxPoint()==10);
		assertTrue(res[0].getCurrentPoint()==10);
	}
	
	@Test
	void testGetCompletedTestsInvalidUser() {
		List<TestFill> testFillList  = new ArrayList<TestFill>();
		List<CompletedTestDTO> dtoList = new ArrayList<CompletedTestDTO>();
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"user5"	,Role.STUDENT);
		user.setId(5L);
		
		when(userRepository.findByIdentifier("user5")).thenReturn(Optional.of(user) );
		when(userRepository.findById(5L)).thenReturn(Optional.of(user) );
		when(testFillRepository.findByUser(user)).thenReturn(testFillList);

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("error","user not found" );
		JSONObject json = this.restTemplate.getForObject("http://localhost:" + port + "/completedtest/2", JSONObject.class);
		assertEquals(jsonObj, json);
	}

}
