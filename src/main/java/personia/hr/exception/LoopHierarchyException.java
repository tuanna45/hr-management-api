package personia.hr.exception;

import java.util.Set;

public class LoopHierarchyException extends RuntimeException {

    public LoopHierarchyException(Set<String> supervisors) {
        super("Loop of employees: " + String.join(", ", supervisors));
    }
}
