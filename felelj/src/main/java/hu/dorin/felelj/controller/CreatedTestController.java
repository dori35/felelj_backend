package hu.dorin.felelj.controller;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.TaskDTO;
import hu.dorin.felelj.dto.TestDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.enums.Type;
import hu.dorin.felelj.model.Choice;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.ChoiceRepository;
import hu.dorin.felelj.repository.TaskRepository;
import hu.dorin.felelj.repository.TestRepository;
import hu.dorin.felelj.repository.UserRepository;
import hu.dorin.felelj.request.TestRequest;
import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/createdtestdtos")
public class CreatedTestController {

	@Autowired
	private TestRepository testRepository;
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private ChoiceRepository choiceRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	


	@GetMapping("/{userId}")
	public ResponseEntity<?> getCreatedTests(@PathVariable("userId") String userId) {
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
		List<TestDTO> testDtoList = new ArrayList<>();
		JSONObject jsonObj = new JSONObject();
		
		if(!userOpt.isPresent())
		{
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		User user = userOpt.get(); 
		if(user.getRole()!=Role.TEACHER)
		{
			jsonObj.put("error","tests not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		List<Test> testList =  user.getCreatedTests();
		testList.sort(Comparator.comparing(o -> o.getCreatedDate()));
		Collections.reverse(testList);
		for (Test t : testList) {
			if(t.getIsActive()) {
				TestDTO tdto = modelMapper.map(t, TestDTO.class);
			    Integer testTimeFrame = 0;
			    Integer testPoint = 0;
			    for(Task task : t.getTasks())
			    {
			    	testTimeFrame+=task.getTimeFrame();
			    	testPoint+=task.getPoint();
			    }
			    tdto.setCreatedDate(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("Europe/Budapest")).format(t.getCreatedDate()));
			    tdto.setTime(testTimeFrame);
			    tdto.setPoint(testPoint);
			    tdto.setTaskNumber(t.getTasks().size());
			    testDtoList.add(tdto);
			}
		}
		return new ResponseEntity<>(testDtoList,HttpStatus.OK);
	}
	
	

	@DeleteMapping("/{userId}/{testId}")
	public ResponseEntity<?> deleteCreatedTest(@PathVariable("userId") String userId, @PathVariable("testId") String testId) {
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));	
		JSONObject jsonObj = new JSONObject();
		
		if(!userOpt.isPresent())
		{
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		User user = userOpt.get();
		
		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));		
		if(!testOpt.isPresent())
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		Test test = testOpt.get();
	
		if(!test.getIsActive())
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		if(	!user.equals(test.getCreatedBy())) {
	
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
			
	   test.setIsActive(false);
	   testRepository.save(test);
	   
	   jsonObj.put("text","successful deleted test" );
	   return new ResponseEntity<>(jsonObj,HttpStatus.OK);
	
   }
	
	@PostMapping("/{userId}/{testId}")
	public ResponseEntity<?> modifyCreatedTest(@PathVariable("userId") String userId, @PathVariable("testId") String testId, @RequestBody TestRequest request) {
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
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));		
		if(!testOpt.isPresent())
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		Test test = testOpt.get();
		/*Test saveTest = modelMapper.map(test, Test.class);*/
		
		
		if(!test.getIsActive())
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
	
		if(	!user.equals(test.getCreatedBy())) {
	
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
			

		if(request.getTitle().length()<=0 || request.getTitle().length()>20) {
	
			jsonObj.put("error","invalid title" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
		
		if(request.getSubject().length()<=0 || request.getSubject().length()>20) {
			
			jsonObj.put("error","invalid subject" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
			
		
		test.setIsActive(false);
		testRepository.save(test);
		
		Test newTest = new Test( request.getTitle(),request.getSubject(), request.getRandom());
		newTest.setCreatedBy(user);
		testRepository.save(newTest);
		
		List<Task> newTasks = new ArrayList<Task>();
		Task task  = null ;
		for (TaskDTO taskdto : request.getTasks()) {
			
		
			 if(taskdto.getPoint()< 1 || taskdto.getPoint() > 100) {
				
				jsonObj.put("error","invalid task point" );
				return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
			 }
			
			 if (taskdto.getTimeFrame() < 5 || taskdto.getTimeFrame() > 20) {
				 jsonObj.put("error","invalid task timeFrame" );
				 return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
			 }
			
			if(taskdto.getText().length()<=0  || taskdto.getText().length()>200) {
				
				jsonObj.put("error","invalid taskText" );
				return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
			}
			
			
			taskdto.setId(null);
			task = modelMapper.map(taskdto, Task.class);
			
			task.setTest(newTest);  
			newTasks.add(task);
			taskRepository.save(task);
			 

			if(task.getTaskType()!=Type.TRUE_FALSE && taskdto.getChoices().size()!=4) {

				jsonObj.put("error","invalid task" );
				return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
			}
			
			
			if(task.getChoices()!=null) {			 
			 
				for (Choice choice : task.getChoices())
				{
					if(choice.getText()==null ) {
						jsonObj.put("error","invalid task" );
						return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
					}else if(choice.getText().length()<=0 || choice.getText().length()>20) {
						jsonObj.put("error","invalid task" );
						return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
					}
					
					choice.setId(null);
					choice.setTask(task);
				}
				choiceRepository.saveAll( task.getChoices());
			}

		
			if(task.getTaskType()==Type.TRUE_FALSE) {
				
				if(taskdto.getSolutionTrueFalse()!=null &&  (taskdto.getSolutionTrueFalse().equals("0") || taskdto.getSolutionTrueFalse().equals("1"))) 
				{
					task.setSolution(taskdto.getSolutionTrueFalse());
					
				}else {
					jsonObj.put("error","invalid task" );
					return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
				}
				
			}else if(task.getTaskType()==Type.ORDER_LIST){
				

				List<String> choiceIdList = new ArrayList<String>();
				
				for (Choice choice : task.getChoices()) {
					
					choiceIdList.add(Long.toString(choice.getId()));
						
				}
				
				task.setSolution(choiceIdList.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")));
				
				
			}else if(task.getTaskType()==Type.ONE_CHOICE){
				if(taskdto.getSolutionOneChoice()!=null &&  (taskdto.getSolutionOneChoice().equals("0") || taskdto.getSolutionOneChoice().equals("1") 
						|| taskdto.getSolutionOneChoice().equals("2") || taskdto.getSolutionOneChoice().equals("3")))
				{
					
					
					Choice choice = task.getChoices().get(Integer.parseInt(taskdto.getSolutionOneChoice()));
					if(choice==null) {
						jsonObj.put("error","invalid task" );
						return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
					}
					Long choiceId = choice.getId();
					task.setSolution(Long.toString(choiceId));
					
					
				}else {
					jsonObj.put("error","invalid task" );
					return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
				}
			}else if(task.getTaskType()==Type.MULTIPLE_CHOICES) {
				

				List<String> choiceIdList = new ArrayList<String>();
				
				if(taskdto.getSolutionMultipleChoices()!=null){
					for (String choiceIndexString : taskdto.getSolutionMultipleChoices()) {
						
						if(choiceIndexString.equals("0") || choiceIndexString.equals("1") || 
								choiceIndexString.equals("2") || choiceIndexString.equals("3") ) {

							Choice choice= task.getChoices().get(Integer.parseInt(choiceIndexString)) ;
							if(choice==null) {
								jsonObj.put("error","invalid task" );
								return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
							}
							Long choiceId = choice.getId();
							choiceIdList.add(Long.toString(choiceId));
						}else {
							jsonObj.put("error","invalid task" );
							return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
						}
					}
					
				task.setSolution(choiceIdList.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")));
					
					
				}else {
					jsonObj.put("error","invalid task" );
					return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
				}
			
			}else {
				jsonObj.put("error","invalid task" );
				return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
			}
		}
		
		taskRepository.saveAll(newTasks);
		testRepository.save(newTest);
		
		TestDTO tdto = modelMapper.map(newTest, TestDTO.class);
		return new ResponseEntity<>(tdto,HttpStatus.OK);
   }
	
	
	@PostMapping("/newTest/{userId}")
	public ResponseEntity<?> createNewTest(@PathVariable("userId") String userId,  @RequestBody TestRequest request) {
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
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		

		if(request.getTitle().length()<=0 || request.getTitle().length()>20) {
			
			jsonObj.put("error","invalid title" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
		
		if(request.getSubject().length()<=0 || request.getSubject().length()>20) {
			
			jsonObj.put("error","invalid subject" );
			return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
		}
		
	
		Test newTest = new Test( request.getTitle(),request.getSubject(), request.getRandom());
		newTest.setCreatedBy(user);
		testRepository.save(newTest);
		
		List<Task> newTasks = new ArrayList<Task>();
		Task task  = null ;
		
		for (TaskDTO taskdto : request.getTasks()) {
			
			

			 if(taskdto.getPoint()< 1 || taskdto.getPoint() > 100) {
				
				jsonObj.put("error","invalid task point" );
				return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
			 }
			
			 if (taskdto.getTimeFrame() < 5 || taskdto.getTimeFrame() > 20) {
				 jsonObj.put("error","invalid task timeFrame" );
				 return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
			 }
			
			if(taskdto.getText().length()<=0  || taskdto.getText().length()>200) {
				
				jsonObj.put("error","invalid taskText" );
				return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
			}
			
			
			taskdto.setId(null);
			task = modelMapper.map(taskdto, Task.class);
			
			task.setTest(newTest);  
			newTasks.add(task);
			taskRepository.save(task);
			 

			if(task.getTaskType()!=Type.TRUE_FALSE && taskdto.getChoices().size()!=4) {

				jsonObj.put("error","invalid task" );
				return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
			}
			
			
			if(task.getChoices()!=null) {			 
			 
				for (Choice choice : task.getChoices())
				{
					if(choice.getText()==null ) {
						jsonObj.put("error","invalid task" );
						return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
					}else if(choice.getText().length()<=0 || choice.getText().length()>20) {
						jsonObj.put("error","invalid task" );
						return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
					}
					
					choice.setId(null);
					choice.setTask(task);
				}
				choiceRepository.saveAll( task.getChoices());
			}

		
			if(task.getTaskType()==Type.TRUE_FALSE) {
				
				if(taskdto.getSolutionTrueFalse()!=null &&  (taskdto.getSolutionTrueFalse().equals("0") || taskdto.getSolutionTrueFalse().equals("1"))) 
				{
					task.setSolution(taskdto.getSolutionTrueFalse());
					
				}else {
					jsonObj.put("error","invalid task" );
					return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
				}
				
			}else if(task.getTaskType()==Type.ORDER_LIST){
				

				List<String> choiceIdList = new ArrayList<String>();
				
				for (Choice choice : task.getChoices()) {
					
					choiceIdList.add(Long.toString(choice.getId()));
						
				}
				
				task.setSolution(choiceIdList.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")));
				
				
			}else if(task.getTaskType()==Type.ONE_CHOICE){
				if(taskdto.getSolutionOneChoice()!=null &&  (taskdto.getSolutionOneChoice().equals("0") || taskdto.getSolutionOneChoice().equals("1") 
						|| taskdto.getSolutionOneChoice().equals("2") || taskdto.getSolutionOneChoice().equals("3")))
				{
					
					
					Choice choice = task.getChoices().get(Integer.parseInt(taskdto.getSolutionOneChoice()));
					if(choice==null) {
						jsonObj.put("error","invalid task" );
						return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
					}
					Long choiceId = choice.getId();
					task.setSolution(Long.toString(choiceId));
					
					
				}else {
					jsonObj.put("error","invalid task" );
					return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
				}
			}else if(task.getTaskType()==Type.MULTIPLE_CHOICES) {
				

				List<String> choiceIdList = new ArrayList<String>();
				
				if(taskdto.getSolutionMultipleChoices()!=null){
					for (String choiceIndexString : taskdto.getSolutionMultipleChoices()) {
						
						if(choiceIndexString.equals("0") || choiceIndexString.equals("1") || 
								choiceIndexString.equals("2") || choiceIndexString.equals("3") ) {

							Choice choice= task.getChoices().get(Integer.parseInt(choiceIndexString)) ;
							if(choice==null) {
								jsonObj.put("error","invalid task" );
								return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
							}
							Long choiceId = choice.getId();
							choiceIdList.add(Long.toString(choiceId));
						}else {
							jsonObj.put("error","invalid task" );
							return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
						}
					}
					
				task.setSolution(choiceIdList.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")));
					
					
				}else {
					jsonObj.put("error","invalid task" );
					return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
				}
			
			}else {
				jsonObj.put("error","invalid task" );
				return new ResponseEntity<>(jsonObj,HttpStatus.BAD_REQUEST);
			}
		}
		
		taskRepository.saveAll(newTasks);
		testRepository.save(newTest);
		
		TestDTO tdto = modelMapper.map(newTest, TestDTO.class);
		return new ResponseEntity<>(tdto,HttpStatus.OK);
  }
}
