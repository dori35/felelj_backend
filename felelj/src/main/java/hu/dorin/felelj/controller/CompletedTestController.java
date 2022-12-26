package hu.dorin.felelj.controller;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.CompletedTaskDTO;
import hu.dorin.felelj.dto.CompletedTestDTO;
import hu.dorin.felelj.enums.Type;
import hu.dorin.felelj.model.Answer;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.TestFill;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.TestFillRepository;
import hu.dorin.felelj.repository.UserRepository;
import net.minidev.json.JSONObject;

@RestController
public class CompletedTestController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TestFillRepository testFillRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@GetMapping("/completedtestdtos/{userId}")
	public ResponseEntity<?> getCompletedTests(@PathVariable("userId") String userId) {
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));		
		JSONObject jsonObj = new JSONObject();
		
		if(!userOpt.isPresent())
		{
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		User user = userOpt.get();
		List<TestFill> testFillList = testFillRepository.findByUser(user);
		
		List<CompletedTestDTO> dtoList = new ArrayList<CompletedTestDTO>(); 
		testFillList.sort(Comparator.comparing(o -> o.getFillDate()));
		Collections.reverse(testFillList);
		for (TestFill testFill : testFillList) {
			Test test = testFill.getTest();
			CompletedTestDTO completedTestDto = modelMapper.map(test, CompletedTestDTO.class);
			completedTestDto.setCurrentPoint(testFill.getPoint());
			
			Integer testTimeFrame=0;
			Integer testPoint=0;
			List<CompletedTaskDTO> completedTasksList = new ArrayList<CompletedTaskDTO>(); 
			for(Task task : test.getTasks())
			{
				testTimeFrame+=task.getTimeFrame();
				testPoint+=task.getPoint();

				CompletedTaskDTO completedTaskDTO  = modelMapper.map(task, CompletedTaskDTO.class);

				Answer answer = new Answer();
				for (Answer a : testFill.getAnswers()) {
					if(a.getTask().equals(task))
					{
						answer=a;
					}
				}
				
				int currentPoint = 0;
				if(task.getTaskType()==Type.MULTIPLE_CHOICES) {
					List<String> listOfGoodAnswers =  Arrays.asList(task.getSolution().split(","));
					List<String> listOfUserAnswers =  Arrays.asList(answer.getAnswer().split(","));
					
					Collections.sort(listOfGoodAnswers);
					Collections.sort(listOfUserAnswers);
					
					if( Objects.equals(listOfGoodAnswers,listOfUserAnswers)){
						currentPoint=task.getPoint();
					}
					
				}else {
					if(task.getSolution().equals(answer.getAnswer())) {
						currentPoint = task.getPoint();
					}
				}
				
				completedTaskDTO.setCurrentPoint(currentPoint);
				completedTaskDTO.setAnswer(answer.getAnswer()) ;
				completedTasksList.add(completedTaskDTO);
				
			}
			completedTestDto.setMaxPoint(testPoint);
			completedTestDto.setTimeFrame(testTimeFrame);
			completedTestDto.setFillDate(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("Europe/Budapest")).format(testFill.getFillDate()));	
			completedTestDto.setTaskNumber(test.getTasks().size());	
			completedTestDto.setTasks(completedTasksList);

		
			
			dtoList.add(completedTestDto);
		}
		return new ResponseEntity<>(dtoList,HttpStatus.OK);
	}
}
