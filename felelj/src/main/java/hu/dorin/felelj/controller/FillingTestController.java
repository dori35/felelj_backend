package hu.dorin.felelj.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.FillingTaskDTO;
import hu.dorin.felelj.dto.FillingTestDTO;
import hu.dorin.felelj.enums.Type;
import hu.dorin.felelj.model.Answer;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.TestFill;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.AnswerRepository;
import hu.dorin.felelj.repository.TaskRepository;
import hu.dorin.felelj.repository.TestFillRepository;
import hu.dorin.felelj.repository.TestRepository;
import hu.dorin.felelj.repository.UserRepository;
import hu.dorin.felelj.request.AnswerRequest;
import hu.dorin.felelj.request.FillingTestRequest;
import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/fillingtestdtos")
public class FillingTestController {
	@Autowired
	private TestRepository testRepository;
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AnswerRepository answerRepository;
	
	@Autowired
	private TestFillRepository testFillRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@GetMapping("/{testId}/{userId}")
	public ResponseEntity<?> getFillingTest(@PathVariable("testId") String testId,@PathVariable("userId") String userId) {
		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
		JSONObject jsonObj = new JSONObject();
		if(!testOpt.isPresent())
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		if(!userOpt.isPresent())
		{
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		Test test = testOpt.get();
		User user = userOpt.get();
		
		if(!user.equals(test.getCreatedBy())){
		
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
			
		if(!test.getIsActive())
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		if(test.getTasks().size()<1)
		{
			jsonObj.put("error","task number invalid" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		FillingTestDTO ftdto = modelMapper.map(test, FillingTestDTO.class);
		Integer testTimeFrame = 0;
		Integer testPoint = 0;
		for(Task task : test.getTasks())
		{
			testTimeFrame+=task.getTimeFrame();
			testPoint+=task.getPoint();
			if(task.getTaskType()==Type.ORDER_LIST) {
				
			}
		}
		ftdto.setTime(testTimeFrame);
		ftdto.setPoint(testPoint);
		ftdto.setTaskNumber(test.getTasks().size());
		
		for(FillingTaskDTO taskdto : ftdto.getTasks())
		{
			if(taskdto.getTaskType()==Type.ORDER_LIST) {
				
				Collections.shuffle(taskdto.getChoices());
			}
		}
		
		return new ResponseEntity<>(ftdto,HttpStatus.OK);
	}
	
	@PostMapping("/{testId}/{userId}")
	public ResponseEntity<?> processFillingTest(@PathVariable("testId") String testId,@PathVariable("userId") String userId,@RequestBody FillingTestRequest request) {
		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));
		JSONObject jsonObj = new JSONObject();
		
		if(!testOpt.isPresent())
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		Test test = testOpt.get();
		if(!test.getIsActive())
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
		
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
		if(!userOpt.isPresent())
		{
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
		
		User user = userOpt.get();
		
		List<Answer> answerList = new ArrayList<Answer>();
		Integer totalPoints = 0;
		TestFill testFill = new TestFill( test, user,request.getStartDate());
		testFillRepository.save(testFill);
		for ( AnswerRequest answerToTask : request.getAnswers()) {
			
			Optional<Task> taskOpt = taskRepository.findById(answerToTask.getId());
			if(!taskOpt.isPresent())
			{
				jsonObj.put("error","answer not found" );
				return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
			}
			
			Task task = taskOpt.get();
			
			Answer answer = new Answer();
			String answerText = answerToTask.getAnswer();
			
			if(task.getTaskType()==Type.MULTIPLE_CHOICES) {
				List<String> listOfGoodAnswers =  Arrays.asList(task.getSolution().split(","));
				List<String> listOfUserAnswers =  Arrays.asList(answerText.split(","));
				
				Collections.sort(listOfGoodAnswers);
				Collections.sort(listOfUserAnswers);
				
				if( Objects.equals(listOfGoodAnswers,listOfUserAnswers)){
					totalPoints+=task.getPoint();
				}
				answer.setAnswer(answerText);
			}else {
			
				if(answerText.equals(task.getSolution()))
				{
					totalPoints+=task.getPoint();
				}
				answer.setAnswer(answerText);
				
			}
		
			answer.setTask(task);
			answer.setTestFill(testFill);
			answerRepository.save(answer);
			answerList.add(answer);
		}
		testFill.setAnswers(answerList);
		testFill.setPoint(totalPoints);
		testFillRepository.save(testFill);

		jsonObj.put("text","successful sent test" );
		return new ResponseEntity<>(jsonObj,HttpStatus.OK);
	}

}
