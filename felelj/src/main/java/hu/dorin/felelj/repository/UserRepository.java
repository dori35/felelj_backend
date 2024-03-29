package hu.dorin.felelj.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import hu.dorin.felelj.model.User;

@RepositoryRestResource
public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findByIdentifier(String identifier);

}
