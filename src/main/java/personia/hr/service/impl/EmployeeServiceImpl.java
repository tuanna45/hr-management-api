package personia.hr.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import personia.hr.domain.Employee;
import personia.hr.exception.InvalidValueException;
import personia.hr.exception.LoopHierarchyException;
import personia.hr.exception.MultipleRootFoundException;
import personia.hr.exception.NoEmployeeFoundException;
import personia.hr.repository.EmployeeRepository;
import personia.hr.service.EmployeeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final int MAX_SUPERVISOR_LEVEL = 2;
    private static final int FIRST_SUPERVISOR_LEVEL = 1;

    private final EmployeeRepository employeeRepository;

    /**
     * Create employees hierarchy
     *
     * @param inputEmployees: Json input describe relationship between employee and supervisor
     * @return Employees hierarchy
     * @throws InvalidValueException:      Invalid input value found
     * @throws MultipleRootFoundException: Multiple top supervisor found
     * @throws LoopHierarchyException:     Input hierarchy contain loops
     */
    @Override
    public Map<String, Object> createEmployees(Map<String, String> inputEmployees) {
        validateInputEmployees(inputEmployees);

        Map<String, List<String>> supervisors = getSupervisors(inputEmployees);
        List<String> topSupervisors = getTopSupervisors(inputEmployees, supervisors);

        if (hasMultipleRoots(topSupervisors)) {
            throw new MultipleRootFoundException(topSupervisors);
        }

        if (hasLoopHierarchy(topSupervisors)) {
            throw new LoopHierarchyException(supervisors.keySet());
        }

        saveEmployeesInDB(inputEmployees);

        return buildHighestSupervisorHierarchy(supervisors, topSupervisors);
    }

    private void validateInputEmployees(Map<String, String> inputEmployees) {
        // Check input is empty or not
        if (CollectionUtils.isEmpty(inputEmployees)) {
            throw new InvalidValueException();
        }

        // Validate value of each employee
        inputEmployees.entrySet().stream()
                .filter(it -> StringUtils.isEmpty(it.getValue()) || it.getKey().equalsIgnoreCase(it.getValue()))
                .findFirst()
                .ifPresent(it -> {
                    throw new InvalidValueException(it.getKey());
                });
    }

    // Get a supervisors's structure contains key is supervisor and value is supervisor's employees
    private Map<String, List<String>> getSupervisors(Map<String, String> employees) {
        return employees.entrySet().stream()
                .collect(groupingBy(Map.Entry::getValue, mapping(Map.Entry::getKey, toList())));
    }

    // Get top supervisor - Top supervisor is the person who is not an employee of another supervisor
    private List<String> getTopSupervisors(Map<String, String> inputEmployees,
                                           Map<String, List<String>> supervisors) {
        return supervisors.keySet().stream()
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
    public Map<String, Object> getSpecifiedEmployee(String employeeName) {
        Map<String, String> employeeMap = getEmployeesFromDB();

        if (employeeMap.containsKey(employeeName) == FALSE) {
            throw new NoEmployeeFoundException();
        }

        return buildSupervisorHierarchyByEmployee(employeeName, employeeMap, FIRST_SUPERVISOR_LEVEL);
    }

    // Get employees from DB and create map with key is employee and value is supervisor
    private Map<String, String> getEmployeesFromDB() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().collect(toMap(Employee::getEmployeeName, Employee::getSupervisorName));
    }

    // Create hierarchy of employee's supervisor
    private Map<String, Object> buildSupervisorHierarchyByEmployee(String employeeName, Map<String, String> employeeMap, int count) {
        Map<String, Object> employee = new HashMap<>();

        if (count <= MAX_SUPERVISOR_LEVEL) {
            Optional.ofNullable(employeeMap.get(employeeName))
                    .ifPresent(supervisor -> employee.put(supervisor, buildSupervisorHierarchyByEmployee(supervisor, employeeMap, count + 1)));
        }

        return employee;
    }

    /**
     * Get employees hierarchy
     *
     * @return Hierarchy of all employees
     */
    @Override
    public Map<String, Object> getEmployees() {
        Map<String, String> employeeMap = getEmployeesFromDB();
        Map<String, List<String>> supervisors = getSupervisors(employeeMap);
        List<String> topSupervisors = getTopSupervisors(employeeMap, supervisors);

        return buildHighestSupervisorHierarchy(supervisors, topSupervisors);
    }

}
