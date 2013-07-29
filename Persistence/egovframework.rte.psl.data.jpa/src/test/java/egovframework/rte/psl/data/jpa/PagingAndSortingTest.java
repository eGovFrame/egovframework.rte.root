package egovframework.rte.psl.data.jpa;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import egovframework.rte.psl.data.jpa.domain.Employee;
import egovframework.rte.psl.data.jpa.repository.EmployeeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/spring/context-*.xml")
@Transactional
public class PagingAndSortingTest {
	private static Logger logger = Logger.getLogger(PagingAndSortingTest.class);

	@Autowired
	EmployeeRepository repository;
	
	private static final String[] NAME = {"Alice", "Bob", "Charles", "Dennis", "Emily", "Frank ", "George", "Henry"};
	
	@Before
	public void setUp() {
		// no-op
	}


	@Test
	public void testSelectList() {
		
		Employee[] employees = getEmployeeList();
		
		for (int i = 0; i < employees.length; i++) {
			employees[i] = repository.save(employees[i]);
		}
		
		int size = 3;
		Page<Employee> list = null;
		
		Sort sort = new Sort(new Order(Sort.Direction.ASC, "employeeName"));
		
		int totalIndex = 0;
		
		for (int i = 0; i < (NAME.length + (size - 1)) / size ; i++) {	// 올림 처리
			list = repository.findAll(new PageRequest(i, size, sort));
			
			logger.debug("Number of current page's element : " + list.getNumberOfElements());
			for (Employee employee : list) {
				logger.debug("Selected employee name : " + employee.getEmployeeName());
				
				assertEquals(NAME[totalIndex++], employee.getEmployeeName());
			}

			assertEquals((i == NAME.length / size ? NAME.length % size : size), list.getNumberOfElements());
		}
	}
	
	private Employee[] getEmployeeList() {
		List<Employee> employees = new ArrayList<Employee>();
		
		Employee employee = null;
		for (int i = 0; i < NAME.length; i++) {
			employee = new Employee();
			
			employee.setEmployeeName(NAME[i]);
			employee.setHireDate(new Date());
			
			employees.add(employee);
		}
		
		return employees.toArray(new Employee[0]);
	}


}
