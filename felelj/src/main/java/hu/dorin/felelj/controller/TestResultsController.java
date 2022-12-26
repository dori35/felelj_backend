package hu.dorin.felelj.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.CompletedTaskDTO;
import hu.dorin.felelj.dto.TestResultDTO;
import hu.dorin.felelj.dto.UserResultDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.enums.Type;
import hu.dorin.felelj.model.Answer;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.TestFill;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.TestFillRepository;
import hu.dorin.felelj.repository.TestRepository;
import hu.dorin.felelj.repository.UserRepository;
import net.minidev.json.JSONObject;

@RestController
public class TestResultsController {

	@Autowired
	private TestRepository testRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TestFillRepository testFillRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	

	@GetMapping("/testresults/{testId}/{userId}")
	public ResponseEntity<?> getTestResults(@PathVariable("testId") String testId,@PathVariable("userId") String userId) {
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));	
		JSONObject jsonObj = new JSONObject();
		
		if(!userOpt.isPresent())
		{
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		User user = userOpt.get();
		
		if(user.getRole()!=Role.TEACHER)
		{
			jsonObj.put("error","invalid role" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}

		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));		
		if(!testOpt.isPresent())
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		Test test = testOpt.get();
		
		if(!test.getCreatedBy().equals(user))
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		Integer testTimeFrame = 0;
		Integer testPoint = 0;
		for(Task task : test.getTasks())
		{
			testTimeFrame+=task.getTimeFrame();
			testPoint+=task.getPoint();
		}
		Integer taskNumber = test.getTasks().size();
		
		
		if(test.getTasks().size()<1)
		{
			jsonObj.put("error","task number invalid" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		List<TestFill> testFillList = testFillRepository.findByTest(test);
		testFillList.sort(Comparator.comparing(o -> o.getFillDate()));
		Collections.reverse(testFillList);

        Map<String, List<TestFill>> filledByMinute = new HashMap<>();        
        
        for (TestFill f : testFillList) {
        	if(f.getStartDate()!=null)
        	{
        	
	            String formattedTime = f.getStartDate();
	            
	            if (!filledByMinute.containsKey(formattedTime)) {
	                filledByMinute.put(formattedTime, new ArrayList<>());
	            }
	
	            filledByMinute.get(formattedTime).add(f);
        	}
        }
        
        List<TestResultDTO> testResultDTOList = new ArrayList<TestResultDTO>();
        for (Entry<String, List<TestFill>> entry : filledByMinute.entrySet()) {
            
            double avgPoint = entry.getValue().stream().
                    mapToInt(f -> f.getPoint()).average().orElse(0);
            int bestPoint = entry.getValue().stream().mapToInt(TestFill::getPoint).max().orElse(0);
            int lestPoint = entry.getValue().stream().mapToInt(TestFill::getPoint).min().orElse(0);
            int fillersNumber = entry.getValue().size();

            List<UserResultDTO> userResultDTOList= new  ArrayList<UserResultDTO>();
            for (TestFill testFill : entry.getValue()) {

                UserResultDTO userResultDTO= new UserResultDTO();

                userResultDTO.setUserId( testFill.getUser().getId());
                userResultDTO.setIdentifier( testFill.getUser().getIdentifier());
                userResultDTO.setPoints(testFill.getPoint());
             

				Answer answer = new Answer() ;

	    		List<CompletedTaskDTO> completedTasksList= new ArrayList<CompletedTaskDTO>();
				CompletedTaskDTO completedTaskDTO = new CompletedTaskDTO();
				int currentPoint = 0;
				for(Task task: test.getTasks()) {
					

					completedTaskDTO  = modelMapper.map(task, CompletedTaskDTO.class);
					
					
					for (Answer a : testFill.getAnswers()) {
						if(a.getTask().equals(task))
						{
							answer=a;
						}
					}
					currentPoint = 0;
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

				userResultDTO.setTasks(completedTasksList);
	            userResultDTOList.add(userResultDTO);
            
        	}
            testResultDTOList.add(new TestResultDTO(entry.getKey(), avgPoint, bestPoint,lestPoint,fillersNumber,test.getId(),test.getTitle(),test.getSubject(),test.getRandom(),testTimeFrame,testPoint,taskNumber,userResultDTOList));
        }
        
        return new ResponseEntity<>(testResultDTOList,HttpStatus.OK);
	}
}
