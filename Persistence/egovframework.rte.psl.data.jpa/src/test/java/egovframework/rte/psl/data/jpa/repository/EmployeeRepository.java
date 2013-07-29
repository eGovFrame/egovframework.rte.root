package egovframework.rte.psl.data.jpa.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import egovframework.rte.psl.data.jpa.domain.Employee;

public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {
	
}
