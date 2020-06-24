package personia.hr.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import personia.hr.domain.Employee;
import personia.hr.exception.InvalidValueException;
import personia.hr.exception.LoopHierarchyException;
import personia.hr.exception.MultipleRootFoundException;
import personia.hr.exception.NoEmployeeFoundException;
import personia.hr.repository.EmployeeRepository;
import personia.hr.service.impl.EmployeeServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    public void shouldGetCorrectExceptionMessageWhenCreateEmployeesWithInputEmpty() {
        // Given
        Map<String, String> inputTestEmployees = new HashMap<>();

        // When
        InvalidValueException exception  = Assertions.assertThrows(InvalidValueException.class,
                () -> employeeService.createEmployees(inputTestEmployees));

        // Then
        Assert.assertEquals(exception.getMessage(), new InvalidValueException().getMessage());
    }

    @Test
    public void shouldGetCorrectExceptionMessageWhenCreateEmployeesWithInvalidEmployeeValue() {
        // Given
        Map<String, String> inputTestEmployees = new HashMap<>();
        inputTestEmployees.put("A", "B");
        inputTestEmployees.put("C", null);

        // When
        InvalidValueException exception  = Assertions.assertThrows(InvalidValueException.class,
                () -> employeeService.createEmployees(inputTestEmployees));

        // Then
        Assert.assertEquals(exception.getMessage(), new InvalidValueException("C").getMessage());
    }

    @Test(expected = MultipleRootFoundException.class)
    public void shouldThrowMultipleRootFoundExceptionWhenCreateEmployees() {
        // Given
        Map<String, String> inputTestEmployees = new HashMap<>();
        inputTestEmployees.put("A", "B");
        inputTestEmployees.put("C", "D");

        // When
        employeeService.createEmployees(inputTestEmployees);
    }

    @Test(expected = LoopHierarchyException.class)
    public void shouldThrowLoopHierarchyExceptionWhenCreateEmployees() {
        // Given
        Map<String, String> inputTestEmployees = new HashMap<>();
        inputTestEmployees.put("A", "B");
        inputTestEmployees.put("B", "A");

        // When
        employeeService.createEmployees(inputTestEmployees);
    }

    @Test
    public void shouldCreateEmployeesHierarchySuccessfully() {
        // Given
        Map<String, String> inputTestEmployees = new HashMap<>();
        inputTestEmployees.put("A", "B");
        inputTestEmployees.put("B", "C");

        // When
        Map<String, Object> employeesHierarchy = employeeService.createEmployees(inputTestEmployees);

        // Then
        Map<String, Object> secondSubHierarchy = new HashMap<>();
        secondSubHierarchy.put("A", new HashMap<>());

        Map<String, Object> firstSubHierarchy = new HashMap<>();
        firstSubHierarchy.put("B", secondSubHierarchy);

        Map<String, Object> expectedEmployeesHierarchy = new HashMap<>();
        expectedEmployeesHierarchy.put("C", firstSubHierarchy);

        Assert.assertEquals(employeesHierarchy, expectedEmployeesHierarchy);
    }

    @Test
    public void shouldGetEmployeesHierarchySuccessfully() {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("A", "B"));
        employees.add(new Employee("B", "C"));

        Mockito.when(employeeRepository.findAll()).thenReturn(employees);

        // When
        Map<String, Object> employeesHierarchy = employeeService.getEmployees();

        // Then
        Map<String, Object> secondSubHierarchy = new HashMap<>();
        secondSubHierarchy.put("A", new HashMap<>());

        Map<String, Object> firstSubHierarchy = new HashMap<>();
        firstSubHierarchy.put("B", secondSubHierarchy);

        Map<String, Object> expectedEmployeesHierarchy = new HashMap<>();
        expectedEmployeesHierarchy.put("C", firstSubHierarchy);

        Assert.assertEquals(employeesHierarchy, expectedEmployeesHierarchy);
    }

    @Test
    public void shouldGetSpecifiedEmployeeHierarchyCorrectly() {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("A", "B"));
        employees.add(new Employee("B", "C"));
        employees.add(new Employee("C", "D"));

        Mockito.when(employeeRepository.findAll()).thenReturn(employees);

        // When
        Map<String, Object> specifiedEmployeeHierarchy = employeeService.getSpecifiedEmployee("A");

        // Then
        Map<String, Object> firstSubHierarchy = new HashMap<>();
        firstSubHierarchy.put("C", new HashMap<>());

        Map<String, Object> expectedEmployeesHierarchy = new HashMap<>();
        expectedEmployeesHierarchy.put("B", firstSubHierarchy);

        Assert.assertEquals(specifiedEmployeeHierarchy, expectedEmployeesHierarchy);
    }

    @Test(expected = NoEmployeeFoundException.class)
    public void shouldThrowNoEmployeeFoundException() {
        // Given
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("A", "B"));
        employees.add(new Employee("B", "C"));

        Mockito.when(employeeRepository.findAll()).thenReturn(employees);

        // When
        employeeService.getSpecifiedEmployee("D");
    }

}
