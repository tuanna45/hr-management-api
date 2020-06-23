package personia.hr.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import personia.hr.domain.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
}
