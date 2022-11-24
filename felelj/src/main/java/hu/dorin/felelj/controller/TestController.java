package hu.dorin.felelj.controller;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.FillingTestDTO;
import hu.dorin.felelj.dto.TestDTO;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.TestRepository;
import hu.dorin.felelj.repository.UserRepository;
import hu.dorin.felelj.request.TestRequest;
import net.minidev.json.JSONObject;

@RestController
public class TestController {
	
	@Autowired
	private TestRepository testRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	@GetMapping("/fillingtestdtos/{testId}")
	public FillingTestDTO getTest(@PathVariable("testId") Long testId) {
		Optional<Test> testOpt = testRepository.findById(testId);
		if(!testOpt.isPresent())
		{
			return null;
		}
		Test test = testOpt.get(); 
		FillingTestDTO testdto = modelMapper.map(test, FillingTestDTO.class);
		return testdto;
	}
	

	@GetMapping("/createdtestdtos/{userId}")
	public List<TestDTO> getTests(@PathVariable("userId") String userId) {
		Optional<User> user = userRepository.findById(Long.parseLong(userId));
		List<TestDTO> testDtoList = new ArrayList<>();
		if(!user.isPresent())
		{
			return testDtoList;
		}
		
		for (Test t : user.get().getCreatedTests()) {
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
	public TestDTO modifyTest(@PathVariable("userId") String userId, @PathVariable("testId") String testId, @RequestBody TestRequest request) {
		Optional<User> userOpt = userRepository.findById(Long.parseLong(userId));		
		if(!userOpt.isPresent())
		{
			return null;
		}
		User user = userOpt.get();
		
		Optional<Test> testOpt = testRepository.findById(Long.parseLong(testId));		
		if(!testOpt.isPresent())
		{
			return null;
		}
		Test test = testOpt.get();
		
		
	
		if(	!user.equals(test.getCreatedBy())) {
	
			return null;
		}
			
		Test newTest = new Test( test.getTitle(),test.getSubject(), test.getRandom());
		newTest.setCreatedBy(user);
		testRepository.save(newTest);
		TestDTO tdto = modelMapper.map(newTest, TestDTO.class);
		return tdto;
   }
	
	@GetMapping("/completedtestdtos/{userId}")
	public List<TestDTO> getCompletedTests(@PathVariable("userId") String userId) {
		Optional<User> user = userRepository.findById(Long.parseLong(userId));
		List<TestDTO> testDtoList = new ArrayList<>();
		if(!user.isPresent())
		{
			return testDtoList;
		}
		
		for (Test t : user.get().getCreatedTests()) {
				TestDTO tdto = modelMapper.map(t, TestDTO.class);
			    tdto.setCreatedDate(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("UTC")).format(t.getCreatedDate()));
				testDtoList.add(tdto);
		}
		return testDtoList;
	}

	
}
