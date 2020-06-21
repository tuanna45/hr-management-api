package personia.hr.service;

import java.util.Map;

public interface EmployeeService {
    Map<String, Object> getSupervisorHierarchy(String employeeName);

    Map<String, Object> getEmployeesHierarchy();

    Map<String, Object> createEmployeesHierarchy(Map<String, String> requestEmployees);
}
