package personia.hr.exception;

import java.util.List;
import java.util.stream.Collectors;

public class MultipleRootFoundException extends RuntimeException {

    public MultipleRootFoundException(List<String> topSupervisors) {
        super("Multiple roots found: " + topSupervisors.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
    }
}
