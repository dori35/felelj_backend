package hu.dorin.felelj;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import hu.dorin.felelj.model.Choice;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.User;
import hu.dorin.felelj.enums.Role;
import hu.dorin.felelj.enums.Type;
import hu.dorin.felelj.repository.AnswerRepository;
import hu.dorin.felelj.repository.ChoiceRepository;
import hu.dorin.felelj.repository.TaskRepository;
import hu.dorin.felelj.repository.TestRepository;
import hu.dorin.felelj.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final TestRepository testRepository;
    private final TaskRepository taskRepository;
    private final ChoiceRepository choiceRepository;
    private final Faker faker;
    

    public DataLoader( UserRepository userRepository,TestRepository testRepository , TaskRepository taskRepository, ChoiceRepository choiceRepository, AnswerRepository answerRepository,Faker faker) {
    	this.userRepository = userRepository;
        this.testRepository = testRepository;
        this.taskRepository = taskRepository;
        this.choiceRepository = choiceRepository;
        this.faker = faker;
    }
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
    	

    Random random = new Random();   
    
     // create 10 rows of fake user
     List<User> users = IntStream.rangeClosed(1,10)
                .mapToObj(i -> new User(
                        faker.name().fullName(),
                        passwordEncoder.encode("1234"),
                        faker.internet().emailAddress(),
                        faker.lorem().characters(6, false, true),
                        Role.values()[random.nextInt(Role.values().length)])
                ).collect(Collectors.toList());

     User user = new User( "Admin Admin",
                        passwordEncoder.encode("1234"),
                        faker.internet().emailAddress(),
                        "admin",
                        Role.values()[0]);
     users.add(user);
     userRepository.saveAll(users);
  
     // create 30 rows of fake test
     List<Test> tests = IntStream.rangeClosed(1,30)
             .mapToObj(i -> new Test(
                     faker.name().title(),
                     faker.lorem().word(),
                     faker.random().nextBoolean())
             ).collect(Collectors.toList());
     
    for (Test test : tests) {
    	
    	User u = users.get(random.nextInt(11));
    	while(u.getRole()==Role.STUDENT)
    	{
    		u = users.get(random.nextInt(11));
    	}
 		test.setCreatedBy(u);  
 	}
    
    testRepository.saveAll(tests);
    
     // create 20 rows of fake task
     List<Task> tasks = IntStream.rangeClosed(1,20)
             .mapToObj(i -> new Task(
                     faker.lorem().sentence(),
                     Type.values()[random.nextInt(Type.values().length)],
                     random.nextInt(10,501),
                     random.nextInt(1, 11),
                     faker.lorem().word())
             ).collect(Collectors.toList());

     Integer trueFalseTaskNumber = 0;
     for (Task task : tasks) {
  		task.setTest(tests.get(random.nextInt(30)));  
  		if(task.getAnswerType()==Type.TRUE_FALSE)
  		{
  	  		trueFalseTaskNumber+=1;
  		}
  	 }
      
     taskRepository.saveAll(tasks);
     
     int choiceNumber = (tasks.size()-trueFalseTaskNumber) *4;
     // create 80 rows of fake choice
     List<Choice> choices = IntStream.rangeClosed(1,choiceNumber)
             .mapToObj(i -> new Choice(
                     faker.lorem().word())
             ).collect(Collectors.toList());
    
     choiceRepository.saveAll(choices);
     int k=-1;
     int r;
     List<Integer> list =  new ArrayList<Integer>(List.of(0,1,2,3));
     Task taskForChoice = null ;
     for (int i = 0; i < (tasks.size()-trueFalseTaskNumber) ; i++) {
    	 k++;
    	 for (int j = 0; j < 4; j++) {
    		 taskForChoice = tasks.get(k);
    		
    		 if(taskForChoice!=null )
    		 {
    			 while(taskForChoice.getAnswerType()==Type.TRUE_FALSE)
        		 {
        			 k++;
        			 taskForChoice = tasks.get(k); 
        		 }
        		 
                 choices.get(i*4+j).setTask(taskForChoice);  
    		 }
    	 }
    	 
    
    	 if(taskForChoice!=null && taskForChoice.getAnswerType()!=Type.TRUE_FALSE)
    	 {
    		 if(taskForChoice.getAnswerType()==Type.ORDER_LIST)
        	 {
				 Collections.shuffle(list);
				 taskForChoice.setSolution( choices.get(i*4+list.get(0)).getText() + ","+ choices.get(i*4+list.get(1)).getText()+ ","+ choices.get(i*4+list.get(2)).getText()+ ","+choices.get(i*4+list.get(3)).getText() );
		 	 
        	 }else if(taskForChoice.getAnswerType()==Type.ONE_CHOICE)
        	 {
        		 taskForChoice.setSolution( choices.get(i*4+random.nextInt(4)).getText());	 
        	 }else if(taskForChoice.getAnswerType()==Type.MULTIPLE_CHOICES)
        	 {
        		 r = random.nextInt(4);
        		 switch (r) {
				case 0:
					taskForChoice.setSolution( choices.get(i*4+random.nextInt(4)).getText());
            
					break;
				case 1:
					Collections.shuffle(list);
					taskForChoice.setSolution( choices.get(i*4+list.get(0)).getText()+ ","+ choices.get(i*4+list.get(0)).getText());
					break;
				case 2:
					Collections.shuffle(list);
					taskForChoice.setSolution( choices.get(i*4+list.get(0)).getText()+ ","+ choices.get(i*4+list.get(1)).getText()+","+ choices.get(i*4+list.get(2)).getText());
					break;
				case 3:
					Collections.shuffle(list);
					taskForChoice.setSolution( choices.get(i*4+list.get(0)).getText()+ ","+ choices.get(i*4+list.get(1)).getText()+","+ choices.get(i*4+list.get(2)).getText()+","+ choices.get(i*4+list.get(3)).getText());
					
					break;

				default:
					break;
				}
        	 }
    	 }
    	 taskRepository.save(taskForChoice);
    	 }
     choiceRepository.saveAll(choices);
     
     
     k=-1;
     for (int i = 0; i < trueFalseTaskNumber ; i++) {
    	 k++;
    	 taskForChoice = tasks.get(k);
    		
    	 if(taskForChoice!=null )
    	 {
    			 while(taskForChoice.getAnswerType()!=Type.TRUE_FALSE)
        		 {
        			 k++;
        			 taskForChoice = tasks.get(k); 
        		 }
                 if(taskForChoice.getAnswerType()==Type.TRUE_FALSE){
        			 taskForChoice.setSolution(Integer.toString(random.nextInt(1)));
        		 }
    		 
    	  }
    	 
    	 if(taskForChoice!=null && taskForChoice.getAnswerType()==Type.TRUE_FALSE)
     	 {
    		 taskForChoice.setSolution(Integer.toString(random.nextInt(1)));
      	 }
      	     	 
      	 taskRepository.save(taskForChoice);
      }
       
     
     }
}
