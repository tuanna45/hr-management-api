package personia.hr.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;
import personia.hr.dto.Employee;
import personia.hr.exception.MultipleRootFoundException;
import personia.hr.exception.NoEmployeeFoundException;
import personia.hr.exception.NoRootFoundException;
import personia.hr.repository.EmployeeRepository;
import personia.hr.service.impl.EmployeeServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test(expected = MultipleRootFoundException.class)
    public void shouldThrowMultipleRootFoundException() {
        // Given
        Map<String, String> employeeTest = new HashMap<>();
        employeeTest.put("A", "B");
        employeeTest.put("C", "D");

        // When
        employeeService.createEmployeesHierarchy(employeeTest);
    }

    @Test(expected = NoRootFoundException.class)
    public void shouldThrowNoRootFoundException() {
        // Given
        Map<String, String> employeeTest = new HashMap<>();
        employeeTest.put("A", "B");
        employeeTest.put("B", "A");

        // When
        employeeService.createEmployeesHierarchy(employeeTest);
    }

    @Test
    public void shouldCreateEmployeesHierarchySuccessfully() {
        // Given
        Map<String, String> employeeTest = new HashMap<>();
        employeeTest.put("A", "B");
        employeeTest.put("B", "C");

        // When
        Map<String, Object> employeesHierarchy = employeeService.createEmployeesHierarchy(employeeTest);

        // Then
        Map<String, Object> secondLevelResult = new HashMap<>();
        secondLevelResult.put("A", new HashMap<>());

        Map<String, Object> firstLevelResult = new HashMap<>();
        firstLevelResult.put("B", secondLevelResult);

        Map<String, Object> finalExpectedResult = new HashMap<>();
        finalExpectedResult.put("C", firstLevelResult);

        Assert.assertEquals(employeesHierarchy, finalExpectedResult);
    }

    @Test
    public void shouldGetEmployeesHierarchyCorrectly() {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("A", "B"));
        employees.add(new Employee("B", "C"));

        Mockito.when(employeeRepository.findAll()).thenReturn(employees);

        // When
        Map<String, Object> employeesHierarchy = employeeService.getEmployeesHierarchy();

        // Then
        Map<String, Object> secondLevelResult = new HashMap<>();
        secondLevelResult.put("A", new HashMap<>());

        Map<String, Object> firstLevelResult = new HashMap<>();
        firstLevelResult.put("B", secondLevelResult);

        Map<String, Object> finalExpectedResult = new HashMap<>();
        finalExpectedResult.put("C", firstLevelResult);

        Assert.assertEquals(employeesHierarchy, finalExpectedResult);
    }

    @Test
    public void shouldGetSupervisorHierarchyCorrectly() {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("A", "B"));
        employees.add(new Employee("B", "C"));

        Mockito.when(employeeRepository.findAll()).thenReturn(employees);

        // When
        Map<String, Object> supervisorHierarchy = employeeService.getSupervisorHierarchy("B");

        // Then
        Map<String, Object> finalExpectedResult = new HashMap<>();
        finalExpectedResult.put("C", new HashMap<>());

        Assert.assertEquals(supervisorHierarchy, finalExpectedResult);
    }

    @Test(expected = NoEmployeeFoundException.class)
    public void shouldThrowNoEmployeeFoundException() {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("A", "B"));
        employees.add(new Employee("B", "C"));

        Mockito.when(employeeRepository.findAll()).thenReturn(employees);

        // When
        employeeService.getSupervisorHierarchy("D");
    }

}
