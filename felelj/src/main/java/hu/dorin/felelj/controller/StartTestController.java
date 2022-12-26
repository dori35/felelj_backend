package hu.dorin.felelj.controller;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.FillingTaskDTO;
import hu.dorin.felelj.dto.FillingTestDTO;
import hu.dorin.felelj.dto.TopDTO;
import hu.dorin.felelj.dto.TopResultsDTO;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.enums.Type;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.TestFill;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.TestFillRepository;
import hu.dorin.felelj.repository.TestRepository;
import hu.dorin.felelj.repository.UserRepository;
import hu.dorin.felelj.request.StartTestRequest;
import net.minidev.json.JSONObject;

@RestController
@RequestMapping("/starttest")
public class StartTestController {

	@Autowired
	private TestRepository testRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private TestFillRepository testFillRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	@GetMapping("/{url}")
	public ResponseEntity<?> getStartTest(@PathVariable("url") String url) {
		Optional<Test> testOpt = testRepository.findByUrlEquals(url);
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
		
		return new ResponseEntity<>(ftdto,HttpStatus.OK);
		}
	

	@GetMapping("/results/{url}/{userId}")
	public ResponseEntity<?> getTopResults(@PathVariable("url") String url, @PathVariable("userId") String userId) {
		Optional<Test> testOptional = testRepository.findByUrlEquals(url);
		JSONObject jsonObj = new JSONObject();
		
		if(!testOptional.isPresent())
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		Test test = testOptional.get();
		if(!test.getIsActive())
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
		if(!userOpt.isPresent())
		{
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		User user = userOpt.get();
	
		TopResultsDTO topResultsDTO = new TopResultsDTO();
		
		Integer testPoint=0;
		for(Task task : test.getTasks())
		{
			testPoint+=task.getPoint();
		}
		
		String startDateString = (DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("Europe/Budapest")).format(test.getStartDate().toInstant()));
		Optional<TestFill> testFillOpt = testFillRepository.findByStartDateEqualsAndUser(startDateString,user);
		
		if(testFillOpt.isEmpty()){
			jsonObj.put("error","filling not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
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
		if(topResultsDTO.getCurrentPoints()==-1) {
			jsonObj.put("error","results not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(topResultsDTO,HttpStatus.OK);
	}


	@PutMapping("/{userId}/{testId}")
	public ResponseEntity<?> settingStartTest(@PathVariable("testId") String testId,@PathVariable("userId") String userId,@RequestBody StartTestRequest request) {
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
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));
		if(!userOpt.isPresent())
		{
			jsonObj.put("error","user not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		User user = userOpt.get();
		if(user.getRole()!=Role.TEACHER)
		{
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
		
		if(!user.equals(test.getCreatedBy())) {
			jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
		}
			
		test.setUrl(request.getUrl());
		
		Integer hours =0;
		Integer minutes =0;
		try{
			 hours = Integer.parseInt(request.getStartTime().substring(0, 2));
			 minutes =  Integer.parseInt(request.getStartTime().substring(3, 5));
	    }catch (NumberFormatException ex){
	    	jsonObj.put("error","test not found" );
			return new ResponseEntity<>(jsonObj,HttpStatus.NOT_FOUND);
	    }
		
		Date date = new Date();
		date.setHours(hours);
		date.setMinutes(minutes);
		date.setSeconds(0);
		test.setStartDate(date);
		testRepository.save(test);
		
		jsonObj.put("text","successful startTest" );
		return ResponseEntity.ok(jsonObj);
	}
	
	
	
}
