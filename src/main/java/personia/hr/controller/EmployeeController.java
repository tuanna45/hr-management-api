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
    public ResponseEntity<Map<String, Object>> getEmployees() {
        return ok(employeeService.getEmployees());
    }

    @GetMapping("/{employeeName}")
    public ResponseEntity<Map<String, Object>> getSpecifiedEmployee(@PathVariable String employeeName) {
        return ok(employeeService.getSpecifiedEmployee(employeeName));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createEmployees(@RequestBody Map<String, String> requestEmployees) {
        return ok(employeeService.createEmployees(requestEmployees));
    }

}
