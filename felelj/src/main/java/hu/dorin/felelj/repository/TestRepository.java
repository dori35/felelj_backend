package hu.dorin.felelj.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import hu.dorin.felelj.model.Test;

@RepositoryRestResource
public interface TestRepository extends CrudRepository<Test, Long> {

}