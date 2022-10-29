package hu.dorin.felelj.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import hu.dorin.felelj.dto.TestDTO;
import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.repository.TestRepository;
import hu.dorin.felelj.repository.UserRepository;

public class TestController {
	
	@Autowired
	private TestRepository testRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
}
