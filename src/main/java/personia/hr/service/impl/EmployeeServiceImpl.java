package personia.hr.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import personia.hr.dto.Employee;
import personia.hr.exception.MultipleRootFoundException;
import personia.hr.exception.NoEmployeeFoundException;
import personia.hr.exception.NoRootFoundException;
import personia.hr.repository.EmployeeRepository;
import personia.hr.service.EmployeeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Override
    public Map<String, Object> createEmployeesHierarchy(Map<String, String> requestEmployees) {
        Map<String, List<String>> supervisorHierarchy = getSupervisorHierarchy(requestEmployees);
        List<String> rootSupervisors = getRootSupervisors(requestEmployees, supervisorHierarchy);

        if (hasMultipleRoots(rootSupervisors)) {
            throw new MultipleRootFoundException();
        }

        if (hasNoRoot(rootSupervisors)) {
            throw new NoRootFoundException();
        }

        saveEmployeesInDB(requestEmployees);
        return buildEmployeeHierarchy(supervisorHierarchy, rootSupervisors);
    }

    private Map<String, List<String>> getSupervisorHierarchy(Map<String, String> employees) {
        return employees.entrySet().stream()
                .collect(groupingBy(Map.Entry::getValue, mapping(Map.Entry::getKey, toList())));
    }

    private List<String> getRootSupervisors(Map<String, String> requestEmployees,
                                            Map<String, List<String>> supervisorHierarchy) {
        return supervisorHierarchy.keySet().stream()
                .filter(supervisor -> isNull(requestEmployees.get(supervisor)))
                .collect(toList());
    }

    private boolean hasNoRoot(List<String> rootSupervisors) {
        return rootSupervisors.size() == 0;
    }

    private boolean hasMultipleRoots(List<String> rootSupervisors) {
        return rootSupervisors.size() > 1;
    }

    private void saveEmployeesInDB(Map<String, String> employeeMap) {
        // Delete all before saving new data
        employeeRepository.deleteAll();
        employeeMap.entrySet().stream()
                .map(it -> Employee.builder()
                        .employeeName(it.getKey())
                        .supervisorName(it.getValue())
                        .build())
                .forEach(employeeRepository::save);
    }

    private Map<String, Object> buildEmployeeHierarchy(Map<String, List<String>> supervisorHierarchy,
                                                       List<String> rootSupervisors) {
        String topSupervisor = rootSupervisors.get(0);
        Map<String, Object> finalHierarchy = new HashMap<>();
        finalHierarchy.put(topSupervisor, getEmployeeMap(topSupervisor, supervisorHierarchy));
        return finalHierarchy;
    }

    private Map<String, Object> getEmployeeMap(String supervisorName,
                                               Map<String, List<String>> supervisorHierarchy) {
        Map<String, Object> employee = new HashMap<>();
        Optional.ofNullable(supervisorHierarchy.get(supervisorName))
                .ifPresent(employees -> employees.forEach(it -> employee.put(it, getEmployeeMap(it, supervisorHierarchy))));
        return employee;
    }

    @Override
    public Map<String, Object> getSupervisorHierarchy(String employeeName) {
        List<Employee> employees = employeeRepository.findAll();
        Map<String, String> employeeMap = getEmployeeMap(employees);

        if (employeeMap.containsKey(employeeName) == FALSE) {
            throw new NoEmployeeFoundException();
        }

        return getSupervisorMap(employeeName, employeeMap);
    }

    private Map<String, String> getEmployeeMap(List<Employee> employees) {
        return employees.stream()
                .collect(toMap(Employee::getEmployeeName, Employee::getSupervisorName));
    }

    private Map<String, Object> getSupervisorMap(String employeeName, Map<String, String> employeeMap) {
        Map<String, Object> employee = new HashMap<>();
        Optional.ofNullable(employeeMap.get(employeeName))
                .ifPresent(supervisor -> employee.put(supervisor, getSupervisorMap(supervisor, employeeMap)));

        return employee;
    }

    @Override
    public Map<String, Object> getEmployeesHierarchy() {
        List<Employee> employees = employeeRepository.findAll();
        Map<String, String> employeeMap = getEmployeeMap(employees);
        Map<String, List<String>> supervisorHierarchy = getSupervisorHierarchy(employeeMap);
        List<String> rootSupervisors = getRootSupervisors(employeeMap, supervisorHierarchy);

        return buildEmployeeHierarchy(supervisorHierarchy, rootSupervisors);
    }

}
