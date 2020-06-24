package personia.hr.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import personia.hr.service.EmployeeService;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Test
    public void shouldCreateEmployeesHierarchySuccessfully() {
        // Given
        Map<String, String> inputTestEmployees = new HashMap<>();
        inputTestEmployees.put("A", "B");
        inputTestEmployees.put("B", "C");

        Map<String, Object> secondSubHierarchy = new HashMap<>();
        secondSubHierarchy.put("A", new HashMap<>());

        Map<String, Object> firstSubHierarchy = new HashMap<>();
        firstSubHierarchy.put("B", secondSubHierarchy);

        Map<String, Object> expectedEmployeesHierarchy = new HashMap<>();
        expectedEmployeesHierarchy.put("C", firstSubHierarchy);

        Mockito.when(employeeService.createEmployees(inputTestEmployees))
                .thenReturn(expectedEmployeesHierarchy);

        // When
        ResponseEntity<Map<String, Object>> employeesHierarchyResponse = employeeController.createEmployees(inputTestEmployees);

        // Then
        Assert.assertEquals(employeesHierarchyResponse.getStatusCode(), OK);
        Assert.assertEquals(employeesHierarchyResponse.getBody(), expectedEmployeesHierarchy);
    }

    @Test
    public void shouldGetEmployeesHierarchySuccessfully() {
        // Given
        Map<String, Object> secondSubHierarchy = new HashMap<>();
        secondSubHierarchy.put("A", new HashMap<>());

        Map<String, Object> firstSubHierarchy = new HashMap<>();
        firstSubHierarchy.put("B", secondSubHierarchy);

        Map<String, Object> expectedEmployeesHierarchy = new HashMap<>();
        expectedEmployeesHierarchy.put("C", firstSubHierarchy);

        Mockito.when(employeeService.getEmployees()).thenReturn(expectedEmployeesHierarchy);

        // When
        ResponseEntity<Map<String, Object>> employeesHierarchyResponse = employeeController.getEmployees();

        // Then
        Assert.assertEquals(employeesHierarchyResponse.getStatusCode(), OK);
        Assert.assertEquals(employeesHierarchyResponse.getBody(), expectedEmployeesHierarchy);
    }

    @Test
    public void shouldGetSpecifiedEmployeeHierarchyCorrectly() {
        // Given
        Map<String, Object> expectedEmployeesHierarchy = new HashMap<>();
        expectedEmployeesHierarchy.put("C", new HashMap<>());

        Mockito.when(employeeService.getSpecifiedEmployee("B"))
                .thenReturn(expectedEmployeesHierarchy);

        // When
        ResponseEntity<Map<String, Object>> specifiedEmployeeHierarchyResponse = employeeController.getSpecifiedEmployee("B");

        // Then
        Assert.assertEquals(specifiedEmployeeHierarchyResponse.getStatusCode(), OK);
        Assert.assertEquals(specifiedEmployeeHierarchyResponse.getBody(), expectedEmployeesHierarchy);
    }

}
