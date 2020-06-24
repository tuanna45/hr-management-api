package personia.hr.service;

import java.util.Map;

public interface EmployeeService {
    Map<String, Object> getSpecifiedEmployee(String employeeName);

    Map<String, Object> getEmployees();

    Map<String, Object> createEmployees(Map<String, String> requestEmployees);
}
