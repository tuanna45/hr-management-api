package personia.hr.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import personia.hr.domain.Employee;
import personia.hr.exception.LoopHierarchyException;
import personia.hr.exception.MultipleRootFoundException;
import personia.hr.exception.NoEmployeeFoundException;
import personia.hr.repository.EmployeeRepository;
import personia.hr.service.EmployeeHierarchyService;

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
public class EmployeeHierarchyServiceImpl implements EmployeeHierarchyService {
    private final EmployeeRepository employeeRepository;

    /**
     * Create employees hierarchy
     *
     * @param inputEmployees: Json input describe relationship between employee and supervisor
     * @return Employees hierarchy
     * @throws MultipleRootFoundException: Multiple top supervisor found
     * @throws LoopHierarchyException:     Input hierarchy contain loops
     */
    @Override
    public Map<String, Object> createEmployeesHierarchy(Map<String, String> inputEmployees) {
        Map<String, List<String>> supervisors = getSupervisors(inputEmployees);
        List<String> topSupervisors = getTopSupervisors(inputEmployees, supervisors);

        if (hasMultipleRoots(topSupervisors)) {
            throw new MultipleRootFoundException();
        }

        if (hasLoopHierarchy(topSupervisors)) {
            throw new LoopHierarchyException();
        }

        saveEmployeesInDB(inputEmployees);

        return buildHighestSupervisorHierarchy(supervisors, topSupervisors);
    }

    // Get a supervisors's structure contains key is supervisor and value is supervisor's employees
    private Map<String, List<String>> getSupervisors(Map<String, String> employees) {
        return employees.entrySet().stream()
                .collect(groupingBy(Map.Entry::getValue, mapping(Map.Entry::getKey, toList())));
    }

    // Get top supervisor - Top supervisor is the person who is not an employee of another supervisor
    private List<String> getTopSupervisors(Map<String, String> inputEmployees,
                                           Map<String, List<String>> supervisorHierarchy) {
        return supervisorHierarchy.keySet().stream()
                .filter(supervisor -> isNull(inputEmployees.get(supervisor)))
                .collect(toList());
    }

    // Check hierarchy contain loop or not
    private boolean hasLoopHierarchy(List<String> topSupervisors) {
        return topSupervisors.size() == 0;
    }

    private boolean hasMultipleRoots(List<String> topSupervisors) {
        return topSupervisors.size() > 1;
    }

    private void saveEmployeesInDB(Map<String, String> employeeMap) {
        // Delete all old data before saving new one
        employeeRepository.deleteAll();

        // Save new employees into DB
        employeeMap.entrySet().stream()
                .map(it -> Employee.builder()
                        .employeeName(it.getKey())
                        .supervisorName(it.getValue())
                        .build())
                .forEach(employeeRepository::save);
    }

    // Create hierarchy of the highest supervisor
    private Map<String, Object> buildHighestSupervisorHierarchy(Map<String, List<String>> supervisors, List<String> topSupervisors) {
        String highestSupervisor = topSupervisors.get(0);
        Map<String, Object> employeesHierarchy = new HashMap<>();
        employeesHierarchy.put(highestSupervisor, buildSubSupervisorHierarchy(highestSupervisor, supervisors));
        return employeesHierarchy;
    }

    // Create hierarchy of the sub supervisor
    private Map<String, Object> buildSubSupervisorHierarchy(String supervisorName, Map<String, List<String>> supervisors) {
        Map<String, Object> employee = new HashMap<>();
        Optional.ofNullable(supervisors.get(supervisorName))
                .ifPresent(employees -> employees.forEach(it -> employee.put(it, buildSubSupervisorHierarchy(it, supervisors))));
        return employee;
    }

    /**
     * Get specified employee hierarchy
     *
     * @param employeeName: Employee name
     * @return Employee hierarchy of specified employee
     * @throws NoEmployeeFoundException: No employee found in DB
     */
    @Override
    public Map<String, Object> getSpecifiedEmployeeHierarchy(String employeeName) {
        Map<String, String> employeeMap = getEmployees();

        if (employeeMap.containsKey(employeeName) == FALSE) {
            throw new NoEmployeeFoundException();
        }

        return buildSupervisorHierarchyByEmployee(employeeName, employeeMap);
    }

    // Create map with key is employee and value is supervisor
    private Map<String, String> getEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().collect(toMap(Employee::getEmployeeName, Employee::getSupervisorName));
    }

    // Create hierarchy of employee's supervisor
    private Map<String, Object> buildSupervisorHierarchyByEmployee(String employeeName, Map<String, String> employeeMap) {
        Map<String, Object> employee = new HashMap<>();
        Optional.ofNullable(employeeMap.get(employeeName))
                .ifPresent(supervisor -> employee.put(supervisor, buildSupervisorHierarchyByEmployee(supervisor, employeeMap)));
        return employee;
    }

    /**
     * Get employees hierarchy
     *
     * @return Hierarchy of all employees
     */
    @Override
    public Map<String, Object> getEmployeesHierarchy() {
        Map<String, String> employeeMap = getEmployees();
        Map<String, List<String>> supervisors = getSupervisors(employeeMap);
        List<String> topSupervisors = getTopSupervisors(employeeMap, supervisors);

        return buildHighestSupervisorHierarchy(supervisors, topSupervisors);
    }

}
