package personia.hr.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @PostMapping
    public ResponseEntity<?> createEmployeeHierarchy(@RequestBody Map<String, String> requestEmployees) {
        Map<String, List<String>> supervisorHierarchy = requestEmployees.entrySet().stream()
                .collect(groupingBy(Map.Entry::getValue, mapping(Map.Entry::getKey, toList())));
        List<String> rootSupervisors = supervisorHierarchy.keySet().stream()
                .filter(supervisor -> Objects.isNull(requestEmployees.get(supervisor)))
                .collect(toList());

        if (rootSupervisors.size() > 1) {
            throw new RuntimeException("Multiple roots found");
        } else if (rootSupervisors.size() == 1) {
            String topSupervisor = rootSupervisors.get(0);
            Map<String, Object> finalHierarchy = new HashMap<>();
            finalHierarchy.put(topSupervisor, getEmployeeMap(topSupervisor, supervisorHierarchy));
            return ResponseEntity.ok(finalHierarchy);
        } else {
            throw new RuntimeException("No top supervisor found");
        }
    }

    private Map<String, Object> getEmployeeMap(String supervisor, Map<String, List<String>> supervisorHierarchy) {
        Map<String, Object> employee = new HashMap<>();

        Optional.ofNullable(supervisorHierarchy.get(supervisor))
                .ifPresent(employees -> employees.forEach(it -> employee.put(it, getEmployeeMap(it, supervisorHierarchy))));

        return employee;
    }
}
