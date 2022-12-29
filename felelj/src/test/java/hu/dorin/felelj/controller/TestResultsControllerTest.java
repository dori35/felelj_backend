package hu.dorin.felelj.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import hu.dorin.felelj.dto.CompletedTaskDTO;
import hu.dorin.felelj.dto.CompletedTestDTO;
import hu.dorin.felelj.dto.TestResultDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.enums.Type;
import hu.dorin.felelj.model.Answer;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.TestFill;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.TestFillRepository;
import hu.dorin.felelj.repository.TestRepository;
import hu.dorin.felelj.repository.UserRepository;
import hu.dorin.felelj.request.LoginRequest;
import hu.dorin.felelj.security.JwtResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestResultsControllerTest {

	@LocalServerPort
    private int port;
	
	@Autowired
	private TestRestTemplate restTemplate;
	
	@MockBean
	private TestRepository testRepository;
	
	@MockBean
	private UserRepository userRepository;
	
	@MockBean
	private TestFillRepository testFillRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Test
	void testResultsController() {
		
		CompletedTaskDTO taskDTO = new CompletedTaskDTO();
		taskDTO.setId(1L);
		taskDTO.setPoint(5);
		taskDTO.setTaskType(Type.TRUE_FALSE);
		taskDTO.setSolution("1");
		taskDTO.setAnswer("0");
		taskDTO.setCurrentPoint(0);
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
		test.setId(7L);
		List<TestFill> testFillList  = new ArrayList<TestFill>();
		TestFill testFill = new TestFill();
		testFill.setId(1L);
		

		List<Answer> answerList = new ArrayList<Answer>();
		
		Answer answer = new Answer();
		answer.setAnswer("0");
		answer.setTask(task1);
		Answer answer2 = new Answer();
		answer2.setAnswer("1");
		answer.setTask(task2);
		answerList.add(answer);
		answerList.add(answer2);
		testFill.setFillDate( Instant.parse("2022-10-03T11:25:30.00Z"));
		testFill.setPoint(0);
		testFill.setTest(test);
		testFill.setAnswers(answerList);
		testFill.setStartDate("2022/9/4 9:59:40");
		testFillList.add(testFill);
		User user = new User("Imelda Jacobs" , "$2a$10$/JJ7DIyeds/yIj.boXMPWuhoL01z7f2zKrD7/yN5Y1gltrSIVk7xi" , "gerry.hudson@gmail.com",	"user1"	,Role.TEACHER);
		user.setId(1L);
		test.setCreatedBy(user);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user) );
		when(testRepository.findById(7L)).thenReturn(Optional.of(test) );
		when(testFillRepository.findByTest(test)).thenReturn(testFillList);
		
	

		Object res = this.restTemplate.getForObject("http://localhost:" + port + "/testresults/7/1", Object.class);
		assertNotNull(res);
		
	}
}
