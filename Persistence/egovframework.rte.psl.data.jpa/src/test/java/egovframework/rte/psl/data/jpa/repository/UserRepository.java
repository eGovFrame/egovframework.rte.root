package egovframework.rte.psl.data.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import egovframework.rte.psl.data.jpa.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {

	User findByUsername(String username);

	List<User> findByLastname(String lastname);

	@Query("select u from User u where u.firstname = ?")
	List<User> findByFirstname(String firstname);

	@Query("select u from User u where u.firstname = :name or u.lastname = :name")
	List<User> findByFirstnameOrLastname(@Param("name") String name);
}
