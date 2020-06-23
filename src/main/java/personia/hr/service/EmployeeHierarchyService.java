package personia.hr.service;

import java.util.Map;

public interface EmployeeHierarchyService {
    Map<String, Object> getSpecifiedEmployeeHierarchy(String employeeName);

    Map<String, Object> getEmployeesHierarchy();

    Map<String, Object> createEmployeesHierarchy(Map<String, String> requestEmployees);
}
