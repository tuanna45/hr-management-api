package personia.hr.exception;

import java.util.Set;
import java.util.stream.Collectors;

public class LoopHierarchyException extends RuntimeException {

    public LoopHierarchyException(Set<String> supervisors) {
        super("Loop of employees: " + supervisors.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
    }
}
