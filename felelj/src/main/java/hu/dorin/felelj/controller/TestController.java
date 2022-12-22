package hu.dorin.felelj.controller;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.CompletedTaskDTO;
import hu.dorin.felelj.dto.CompletedTestDTO;
import hu.dorin.felelj.dto.FillingTaskDTO;
import hu.dorin.felelj.dto.FillingTestDTO;
import hu.dorin.felelj.dto.TaskDTO;
import hu.dorin.felelj.dto.TestDTO;
import hu.dorin.felelj.dto.TestResultDTO;
import hu.dorin.felelj.dto.TopDTO;
import hu.dorin.felelj.dto.TopResultsDTO;
import hu.dorin.felelj.dto.UserResultDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.enums.Type;
import hu.dorin.felelj.model.Answer;
import hu.dorin.felelj.model.Choice;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.TestFill;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.AnswerRepository;
import hu.dorin.felelj.repository.ChoiceRepository;
import hu.dorin.felelj.repository.TaskRepository;
import hu.dorin.felelj.repository.TestFillRepository;
import hu.dorin.felelj.repository.TestRepository;
import hu.dorin.felelj.repository.UserRepository;
import hu.dorin.felelj.request.AnswerRequest;
import hu.dorin.felelj.request.FillingTestRequest;
import hu.dorin.felelj.request.StartTestRequest;
import hu.dorin.felelj.request.TestRequest;
import net.minidev.json.JSONObject;

@RestController
public class TestController {
	
	@Autowired
	private TestRepository testRepository;
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private ChoiceRepository choiceRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AnswerRepository answerRepository;
	
	@Autowired
	private TestFillRepository testFillRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	@GetMapping("/fillingtestdtos/{testId}")
	public FillingTestDTO getFillingTest(@PathVariable("testId") String testId) {
		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));
		if(!testOpt.isPresent())
		{
			return null;
		}
		
		Test test = testOpt.get();
		
		if(!test.getIsActive())
		{
			return null;
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
		
		
		
		return ftdto;
	}
	
	@GetMapping("/starttest/{url}")
	public FillingTestDTO getStartTest(@PathVariable("url") String url) {
		Optional<Test> testOptional = testRepository.findByUrlEquals(url);
		if(!testOptional.isPresent())
		{
			return null;
		}
		Test test = testOptional.get();
		
		if(test==null)
		{
			return null;
		}
		
		
		if(!test.getIsActive())
		{
			return null;
		}
		
		FillingTestDTO ftdto = modelMapper.map(test, FillingTestDTO.class);
		Integer testTimeFrame = 0;
		Integer testPoint = 0;
		for(Task task : test.getTasks())
		{
			testTimeFrame+=task.getTimeFrame();
			testPoint+=task.getPoint();
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
		
		if(test.getStartDate()!=null) {
			ftdto.setStartDate(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("Europe/Budapest")).format(test.getStartDate().toInstant()));	
		}
		
		
		return ftdto;
	}
	

	@GetMapping("/starttest/results/{url}/{userId}")
	public TopResultsDTO getTopResults(@PathVariable("url") String url, @PathVariable("userId") String userId) {

		Optional<Test> testOptional = testRepository.findByUrlEquals(url);
		if(!testOptional.isPresent())
		{
			return null;
		}
		Test test = testOptional.get();
		if(!test.getIsActive())
		{
			return null;
		}
		
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
		if(!userOpt.isPresent())
		{
			return null;
		}
		
		User user = userOpt.get();
	
		TopResultsDTO topResultsDTO = new TopResultsDTO();
		
		Integer testPoint=0;
		for(Task task : test.getTasks())
		{
			testPoint+=task.getPoint();
		}
		
		//try-catch
		String startDateString = (DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("Europe/Budapest")).format(test.getStartDate().toInstant()));
		
		
		Optional<TestFill> testFillOpt = testFillRepository.findByStartDateEqualsAndUser(startDateString,user);
		if(testFillOpt.isEmpty()){
			return null;
		}
		TestFill testFill = testFillOpt.get();
		
		topResultsDTO.setCurrentPoints(testFill.getPoint());
		topResultsDTO.setMaxPoints(testPoint);
		
		
		List<TestFill> testFillList = testFillRepository.findByStartDateEquals(startDateString);

		testFillList.sort(null);
		
		int rank=0;
		int lastPoint = testFillList.get(0).getPoint();
		List<TopDTO> topDTOList =  new ArrayList<TopDTO>();
		List<String> identifiers = new ArrayList<String>();
		
		for (TestFill t : testFillList) {
			
			if(t.getPoint()==lastPoint) {
				identifiers.add(t.getUser().getIdentifier());
			}else {
				topDTOList.add(new TopDTO(lastPoint,identifiers));
				lastPoint= t.getPoint();
				identifiers = new ArrayList<String>();
				identifiers.add(t.getUser().getIdentifier());

				rank++;
				if(rank==3) {
					break;
				}
			}
		}
		
		if(rank<3) {
			topDTOList.add(new TopDTO(lastPoint,identifiers));	
		}
		
		topResultsDTO.setTopThree(topDTOList);
		
		/*if(testFill.getUser().equals(user) && testFill.getStartDate()!=null
				&&  testFill.getStartDate().equals(startDateString)) {
			topResultsDTO.setCurrentPoints(testFill.getPoint());
			topResultsDTO.setMaxPoints(testPoint);
			//top 3
		}*/
		if(topResultsDTO.getCurrentPoints()==-1) {
			//something went wrong
			return null;
		}
		
		
		return topResultsDTO;
	}


	@PutMapping("/starttest/{userId}/{testId}")
	public ResponseEntity<?> settingStartTest(@PathVariable("testId") String testId,@PathVariable("userId") String userId,@RequestBody StartTestRequest request) {
		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));
		if(!testOpt.isPresent())
		{
			return null;
		}
		
		Test test = testOpt.get();
		if(!test.getIsActive())
		{
			return null;
		}
		
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
		if(!userOpt.isPresent())
		{
			return null;
		}
		
		User user = userOpt.get();
		if(user.getRole()!=Role.TEACHER)
		{
			return null;
		}
		
		test.setUrl(request.getUrl());
		
		Integer hours =0;
		Integer minutes =0;
		try{
			 hours = Integer.parseInt(request.getStartTime().substring(0, 2));
			 minutes =  Integer.parseInt(request.getStartTime().substring(3, 5));
	    }catch (NumberFormatException ex){
	         return null;
	    }
		
		Date date = new Date();
		date.setHours(hours);
		date.setMinutes(minutes);
		date.setSeconds(0);
		test.setStartDate(date);
		testRepository.save(test);
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("text","successful startTest" );
		return ResponseEntity.ok(jsonObj);
	}
	
	
	@PostMapping("/fillingtestdtos/{testId}/{userId}")
	public ResponseEntity<?> processFillingTest(@PathVariable("testId") String testId,@PathVariable("userId") String userId,@RequestBody FillingTestRequest request) {
		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));
		if(!testOpt.isPresent())
		{
			return null;
		}
		
		Test test = testOpt.get();
		if(!test.getIsActive())
		{
			return null;
		}
		
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
		if(!userOpt.isPresent())
		{
			return null;
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
				return null;
			}
			
			Task task = taskOpt.get();
			
			Answer answer = new Answer();
			String answerText = answerToTask.getAnswer();
			
			if(task.getTaskType()==Type.MULTIPLE_CHOICES) {
				System.out.println("ALMAAAAA");
				List<String> listOfGoodAnswers =  Arrays.asList(task.getSolution().split(","));
				List<String> listOfUserAnswers =  Arrays.asList(answerText.split(","));
				System.out.println("KENYEEER");
				System.out.println(listOfGoodAnswers);
				System.out.println(listOfUserAnswers);
				
				Collections.sort(listOfGoodAnswers);
				Collections.sort(listOfUserAnswers);
				
				System.out.println(listOfGoodAnswers);
				System.out.println(listOfUserAnswers);
				
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

		JSONObject jsonObj = new JSONObject();
		jsonObj.put("text","successful sent test" );
		return ResponseEntity.ok(jsonObj);
	}
	

	@GetMapping("/createdtestdtos/{userId}")
	public List<TestDTO> getCreatedTests(@PathVariable("userId") String userId) {
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
		List<TestDTO> testDtoList = new ArrayList<>();
		if(!userOpt.isPresent())
		{
			return testDtoList;
		}
		User user = userOpt.get(); 
		if(user.getRole()!=Role.TEACHER)
		{
			return null;
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
		return testDtoList;
	}
	
	

	@DeleteMapping("/createdtestdtos/{userId}/{testId}")
	public ResponseEntity<?> deleteCreatedTest(@PathVariable("userId") String userId, @PathVariable("testId") String testId) {
		
		JSONObject jsonObj = new JSONObject();
		
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));		
		if(!userOpt.isPresent())
		{
			jsonObj.put("text","invalid user" );
			return ResponseEntity.ok(jsonObj);
		}
		User user = userOpt.get();
		
		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));		
		if(!testOpt.isPresent())
		{
			jsonObj.put("text","invalid test" );
			return ResponseEntity.ok(jsonObj);
		}
		Test test = testOpt.get();
	
		if(!test.getIsActive())
		{
			jsonObj.put("text","invalid test" );
			return ResponseEntity.ok(jsonObj);
		}
		
		if(	!user.equals(test.getCreatedBy())) {
	
			jsonObj.put("text","forbidden delete" );
			return ResponseEntity.ok(jsonObj);
		}
			
	   test.setIsActive(false);
	   testRepository.save(test);
	   
	   jsonObj.put("text","successful deleted test" );
	   return ResponseEntity.ok(jsonObj);
	
   }
	
	@PostMapping("/createdtestdtos/{userId}/{testId}")
	public TestDTO modifyCreatedTest(@PathVariable("userId") String userId, @PathVariable("testId") String testId, @RequestBody TestRequest request) {
		
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));		
		if(!userOpt.isPresent())
		{
			return null;
		}
		User user = userOpt.get();
		
		if(user.getRole()!=Role.TEACHER)
		{
			return null;
		}
		
		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));		
		if(!testOpt.isPresent())
		{
			return null;
		}
		Test test = testOpt.get();
		if(!test.getIsActive())
		{
			return null;
		}
		
	
		if(	!user.equals(test.getCreatedBy())) {
	
			return null;
		}
			

		if(request.getTitle().length()<=0 || request.getTitle().length()>20) {
	
			return null;
		}
		
		if(request.getSubject().length()<=0 || request.getSubject().length()>20) {
			
			return null;
		}
			
		
		test.setIsActive(false);
		testRepository.save(test);
		
		Test newTest = new Test( request.getTitle(),request.getSubject(), request.getRandom());
		newTest.setCreatedBy(user);
		testRepository.save(newTest);
		
		List<Task> newTasks = new ArrayList<Task>();
		Task task  = null ;
		for (TaskDTO taskdto : request.getTasks()) {
			
			
			if(taskdto.getText().length()<=0  || taskdto.getText().length()>200) {
				
				return null;
			}
			
			
			taskdto.setId(null);
			task = modelMapper.map(taskdto, Task.class);
			
			task.setTest(newTest);  
			newTasks.add(task);
			taskRepository.save(task);
			 
			
			if(task.getTaskType()!=Type.TRUE_FALSE && taskdto.getChoices().size()!=4) {
				return null;
			}
			
			
			if(task.getChoices()!=null) {			 
			 
				for (Choice choice : task.getChoices())
				{
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
					return null;
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
						return null;
					}
					Long choiceId = choice.getId();
					task.setSolution(Long.toString(choiceId));
					
					
				}else {
					return null;
				}
			}else if(task.getTaskType()==Type.MULTIPLE_CHOICES) {
				

				List<String> choiceIdList = new ArrayList<String>();
				
				if(taskdto.getSolutionMultipleChoices()!=null){
					for (String choiceIndexString : taskdto.getSolutionMultipleChoices()) {
						
						if(choiceIndexString.equals("1") || choiceIndexString.equals("1") || 
								choiceIndexString.equals("2") || choiceIndexString.equals("3") ) {

							Choice choice= task.getChoices().get(Integer.parseInt(choiceIndexString)) ;
							if(choice==null) {
								return null;
							}
							Long choiceId = choice.getId();
							choiceIdList.add(Long.toString(choiceId));
						}else {
							return null;
						}
					}
					
					
				task.setSolution(choiceIdList.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")));
					
					
				}else {
					return null;
				}
			
			}else {
				return null;
			}
		}
		
		taskRepository.saveAll(newTasks);
		testRepository.save(newTest);
		
		TestDTO tdto = modelMapper.map(newTest, TestDTO.class);
		return tdto;
   }
	
	
	@PostMapping("/createdtestdtos/newTest/{userId}")
	public TestDTO createNewTest(@PathVariable("userId") String userId,  @RequestBody TestRequest request) {
		
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));		
		if(!userOpt.isPresent())
		{
			return null;
		}
		User user = userOpt.get();
		
		if(user.getRole()!=Role.TEACHER)
		{
			return null;
		}
		

		if(request.getTitle().length()<=0 || request.getTitle().length()>20) {
	
			return null;
		}
		
		if(request.getSubject().length()<=0 || request.getSubject().length()>20) {
			
			return null;
		}
		
	
		Test newTest = new Test( request.getTitle(),request.getSubject(), request.getRandom());
		newTest.setCreatedBy(user);
		testRepository.save(newTest);
		
		List<Task> newTasks = new ArrayList<Task>();
		Task task  = null ;
		
		for (TaskDTO taskdto : request.getTasks()) {
			
			if(taskdto.getText().length()<=0  || taskdto.getText().length()>200) {
				
				return null;
			}

			taskdto.setId(null);
			task = modelMapper.map(taskdto, Task.class);
			
			task.setTest(newTest);  
			newTasks.add(task);
			taskRepository.save(task);
			
			if(task.getTaskType()!=Type.TRUE_FALSE && taskdto.getChoices().size()!=4) {
				return null;
			}
			
			
			if(task.getChoices()!=null) {			 
			 
				for (Choice choice : task.getChoices())
				{
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
					return null;
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
						return null;
					}
					Long choiceId = choice.getId();
					task.setSolution(Long.toString(choiceId));
					
					
				}else {
					return null;
				}
			}else if(task.getTaskType()==Type.MULTIPLE_CHOICES) {
				

				List<String> choiceIdList = new ArrayList<String>();
				
				if(taskdto.getSolutionMultipleChoices()!=null){
					for (String choiceIndexString : taskdto.getSolutionMultipleChoices()) {
						
						if(choiceIndexString.equals("1") || choiceIndexString.equals("1") || 
								choiceIndexString.equals("2") || choiceIndexString.equals("3") ) {

							Choice choice= task.getChoices().get(Integer.parseInt(choiceIndexString)) ;
							if(choice==null) {
								return null;
							}
							Long choiceId = choice.getId();
							choiceIdList.add(Long.toString(choiceId));
						}else {
							return null;
						}
					}
					
					
				task.setSolution(choiceIdList.stream().map(n -> String.valueOf(n)).collect(Collectors.joining(",")));
					
					
				}else {
					return null;
				}
			
			}else {
				return null;
			}
		}
		
		taskRepository.saveAll(newTasks);
		testRepository.save(newTest);
		
		TestDTO tdto = modelMapper.map(newTest, TestDTO.class);
		return tdto;
   }
	
	@GetMapping("/completedtestdtos/{userId}")
	public List<CompletedTestDTO> getCompletedTests(@PathVariable("userId") String userId) {
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));		
		if(!userOpt.isPresent())
		{
			return null;
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
		return dtoList;
	}

	@GetMapping("/testresults/{testId}/{userId}")
	public List<TestResultDTO> getTestResults(@PathVariable("testId") String testId,@PathVariable("userId") String userId) {

		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));		
		if(!userOpt.isPresent())
		{
			return null;
		}
		User user = userOpt.get();
		if(user.getRole()!=Role.TEACHER)
		{
			return null;
		}

		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));		
		if(!testOpt.isPresent())
		{
			return null;
		}
		Test test = testOpt.get();
		
		if(!test.getCreatedBy().equals(user))
		{
			return null;
		}
		
		Integer testTimeFrame = 0;
		Integer testPoint = 0;
		for(Task task : test.getTasks())
		{
			testTimeFrame+=task.getTimeFrame();
			testPoint+=task.getPoint();
		}
		Integer taskNumber = test.getTasks().size();
		
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
        
        return testResultDTOList;
	}

}
