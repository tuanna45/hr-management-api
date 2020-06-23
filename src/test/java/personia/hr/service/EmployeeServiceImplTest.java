package personia.hr.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import personia.hr.domain.Employee;
import personia.hr.exception.LoopHierarchyException;
import personia.hr.exception.MultipleRootFoundException;
import personia.hr.exception.NoEmployeeFoundException;
import personia.hr.repository.EmployeeRepository;
import personia.hr.service.impl.EmployeeHierarchyServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeHierarchyServiceImpl employeeService;

    @Test(expected = MultipleRootFoundException.class)
    public void shouldThrowMultipleRootFoundException() {
        // Given
        Map<String, String> inputTestEmployees = new HashMap<>();
        inputTestEmployees.put("A", "B");
        inputTestEmployees.put("C", "D");

        // When
        employeeService.createEmployeesHierarchy(inputTestEmployees);
    }

    @Test(expected = LoopHierarchyException.class)
    public void shouldThrowLoopHierarchyException() {
        // Given
        Map<String, String> inputTestEmployees = new HashMap<>();
        inputTestEmployees.put("A", "B");
        inputTestEmployees.put("B", "A");

        // When
        employeeService.createEmployeesHierarchy(inputTestEmployees);
    }

    @Test
    public void shouldCreateEmployeesHierarchySuccessfully() {
        // Given
        Map<String, String> inputTestEmployees = new HashMap<>();
        inputTestEmployees.put("A", "B");
        inputTestEmployees.put("B", "C");

        // When
        Map<String, Object> employeesHierarchy = employeeService.createEmployeesHierarchy(inputTestEmployees);

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
        Map<String, Object> employeesHierarchy = employeeService.getEmployeesHierarchy();

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

        Mockito.when(employeeRepository.findAll()).thenReturn(employees);

        // When
        Map<String, Object> specifiedEmployeeHierarchy = employeeService.getSpecifiedEmployeeHierarchy("B");

        // Then
        Map<String, Object> expectedEmployeesHierarchy = new HashMap<>();
        expectedEmployeesHierarchy.put("C", new HashMap<>());

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
        employeeService.getSpecifiedEmployeeHierarchy("D");
    }

}
