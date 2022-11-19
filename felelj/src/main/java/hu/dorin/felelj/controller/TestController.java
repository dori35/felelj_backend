package hu.dorin.felelj.controller;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import hu.dorin.felelj.dto.FillingTestDTO;
import hu.dorin.felelj.dto.TestDTO;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.TestRepository;
import hu.dorin.felelj.repository.UserRepository;

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
				TestDTO tdto = modelMapper.map(t, TestDTO.class);
			    Integer testTimeFrame = 0;
			    Integer testPoint = 0;
			    for(Task task : t.getTasks())
			    {
			    	testTimeFrame+=task.getTimeFrame();
			    	testPoint+=task.getPoint();
			    }
			    tdto.setCreatedDate(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneId.of("UTC")).format(t.getCreatedDate()));
			    tdto.setTime(testTimeFrame);
			    tdto.setPoint(testPoint);
			    tdto.setTaskNumber(t.getTasks().size());
			    testDtoList.add(tdto);
		}
		return testDtoList;
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
