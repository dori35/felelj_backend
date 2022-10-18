package hu.dorin.felelj;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import hu.dorin.felelj.model.Answer;
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
    private final AnswerRepository answerRepository;
    private final Faker faker;
    

    public DataLoader( UserRepository userRepository,TestRepository testRepository , TaskRepository taskRepository, ChoiceRepository choiceRepository, AnswerRepository answerRepository,Faker faker) {
    	this.userRepository = userRepository;
        this.testRepository = testRepository;
        this.taskRepository = taskRepository;
        this.choiceRepository = choiceRepository;
        this.answerRepository = answerRepository;
        this.faker = faker;
    }
    
    @Override
    public void run(String... args) throws Exception {
    	

    Random random = new Random();   

     // create 10 rows of fake user
     List<User> users = IntStream.rangeClosed(1,10)
                .mapToObj(i -> new User(
                        faker.name().fullName(),
                        faker.internet().password(1, 5, true, false, true),
                        faker.internet().emailAddress(),
                        faker.lorem().characters(6, false, true),
                        Role.values()[random.nextInt(Role.values().length)])
                ).collect(Collectors.toList());

     userRepository.saveAll(users);
        

     // create 10 rows of fake test
     List<Test> tests = IntStream.rangeClosed(1,10)
             .mapToObj(i -> new Test(
                     faker.name().title(),
                     faker.lorem().word(),
                     new Date(),
                     new Date(),
                     faker.random().nextBoolean())
             ).collect(Collectors.toList());
     
    for (Test test : tests) {
 		test.setCreatedBy(users.get(random.nextInt(10)));  
 	}
     
    /*List<Test> t = new ArrayList<Test>(); 
    for (Test test : tests) {
    	List<User> completedTestUseres = new ArrayList<User>();
    	completedTestUseres.add(users.get(random.nextInt(10)));
 		test.setUsers(completedTestUseres);  
 		for (User completedTestUser : completedTestUseres) {
 			t =completedTestUser.getTests();
 			t.add(test);
 			completedTestUser.setTests(t);
		}
 		
 	}*/
     
    // userRepository.saveAll(users);
     testRepository.saveAll(tests);
     
 	
     // create 30 rows of fake task
     List<Task> tasks = IntStream.rangeClosed(1,30)
             .mapToObj(i -> new Task(
                     faker.lorem().sentence(),
                     Type.values()[random.nextInt(Type.values().length)],
                     random.nextInt(1001),
                     random.nextInt(51),
                     faker.lorem().word())
             ).collect(Collectors.toList());

     for (Task task : tasks) {
  		task.setTest(tests.get(random.nextInt(10)));  
  	 }
      
     taskRepository.saveAll(tasks);
     
     
     // create 10 rows of fake answer
     List<Answer> answers = IntStream.rangeClosed(1,10)
             .mapToObj(i -> new Answer(
            		 faker.lorem().sentence())
             ).collect(Collectors.toList());

     for (Answer answer : answers) {
  		answer.setTask(tasks.get(random.nextInt(30)));  
  	 }
     answerRepository.saveAll(answers);

     
     // create 10 rows of fake choice
     List<Choice> choices = IntStream.rangeClosed(1,10)
             .mapToObj(i -> new Choice(
                     faker.lorem().sentence())
             ).collect(Collectors.toList());

     choiceRepository.saveAll(choices);
     
    

    }
}