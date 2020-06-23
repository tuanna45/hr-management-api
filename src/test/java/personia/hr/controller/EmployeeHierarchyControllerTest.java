package personia.hr.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import personia.hr.domain.Employee;
import personia.hr.service.EmployeeHierarchyService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeHierarchyControllerTest {

    @Mock
    private EmployeeHierarchyService employeeHierarchyService;

    @InjectMocks
    private EmployeeHierarchyController employeeHierarchyController;

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

        Mockito.when(employeeHierarchyService.createEmployeesHierarchy(inputTestEmployees))
                .thenReturn(expectedEmployeesHierarchy);

        // When
        ResponseEntity<Map<String, Object>> employeesHierarchyResponse = employeeHierarchyController.createEmployeesHierarchy(inputTestEmployees);

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

        Mockito.when(employeeHierarchyService.getEmployeesHierarchy()).thenReturn(expectedEmployeesHierarchy);

        // When
        ResponseEntity<Map<String, Object>> employeesHierarchyResponse = employeeHierarchyController.getEmployeesHierarchy();

        // Then
        Assert.assertEquals(employeesHierarchyResponse.getStatusCode(), OK);
        Assert.assertEquals(employeesHierarchyResponse.getBody(), expectedEmployeesHierarchy);
    }

    @Test
    public void shouldGetSpecifiedEmployeeHierarchyCorrectly() {
        // Given
        Map<String, Object> expectedEmployeesHierarchy = new HashMap<>();
        expectedEmployeesHierarchy.put("C", new HashMap<>());

        Mockito.when(employeeHierarchyService.getSpecifiedEmployeeHierarchy("B"))
                .thenReturn(expectedEmployeesHierarchy);

        // When
        ResponseEntity<Map<String, Object>> specifiedEmployeeHierarchyResponse = employeeHierarchyController.getSpecifiedEmployeeHierarchy("B");

        // Then
        Assert.assertEquals(specifiedEmployeeHierarchyResponse.getStatusCode(), OK);
        Assert.assertEquals(specifiedEmployeeHierarchyResponse.getBody(), expectedEmployeesHierarchy);
    }

}
