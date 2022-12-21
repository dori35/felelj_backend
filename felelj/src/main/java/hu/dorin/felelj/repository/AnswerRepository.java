package hu.dorin.felelj.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import hu.dorin.felelj.model.Answer;
import hu.dorin.felelj.model.Task;
import hu.dorin.felelj.model.TestFill;
import hu.dorin.felelj.model.User;

@RepositoryRestResource
public interface AnswerRepository extends CrudRepository<Answer, Long> {

}