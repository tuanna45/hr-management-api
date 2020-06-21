package personia.hr.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import personia.hr.service.EmployeeService;

import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getEmployeesHierarchy() {
        return ok(employeeService.getEmployeesHierarchy());
    }

    @GetMapping("/{employeeName}")
    public ResponseEntity<Map<String, Object>> getSupervisorHierarchy(@PathVariable String employeeName) {
        return ok(employeeService.getSupervisorHierarchy(employeeName));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createEmployeeHierarchy(@RequestBody Map<String, String> requestEmployees) {
        return ok(employeeService.createEmployeesHierarchy(requestEmployees));
    }

}
