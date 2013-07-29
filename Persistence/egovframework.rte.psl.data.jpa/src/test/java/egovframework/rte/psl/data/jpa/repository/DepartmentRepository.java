package egovframework.rte.psl.data.jpa.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import egovframework.rte.psl.data.jpa.domain.Department;

public interface DepartmentRepository extends CrudRepository<Department, Long> {

	List<Department> findByDeptNameContaining(String deptname);

}
