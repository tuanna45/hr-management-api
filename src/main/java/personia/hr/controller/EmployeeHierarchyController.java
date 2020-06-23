package personia.hr.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import personia.hr.service.EmployeeHierarchyService;

import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/employees-hierarchy")
public class EmployeeHierarchyController {
    private final EmployeeHierarchyService employeeHierarchyService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getEmployeesHierarchy() {
        return ok(employeeHierarchyService.getEmployeesHierarchy());
    }

    @GetMapping("/{employeeName}")
    public ResponseEntity<Map<String, Object>> getSpecifiedEmployeeHierarchy(@PathVariable String employeeName) {
        return ok(employeeHierarchyService.getSpecifiedEmployeeHierarchy(employeeName));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createEmployeesHierarchy(@RequestBody Map<String, String> requestEmployees) {
        return ok(employeeHierarchyService.createEmployeesHierarchy(requestEmployees));
    }

}
