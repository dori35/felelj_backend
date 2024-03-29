package hu.dorin.felelj.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import hu.dorin.felelj.model.Test;
import hu.dorin.felelj.model.TestFill;
import hu.dorin.felelj.model.User;

@RepositoryRestResource
public interface TestFillRepository extends CrudRepository<TestFill, Long> {

	List<TestFill> findByUser(User user);
	List<TestFill> findByTest(Test test);
	Optional<TestFill> findByStartDateEqualsAndUser(String startDateString,User user);
	List<TestFill> findByStartDateEquals(String startDateString);
}