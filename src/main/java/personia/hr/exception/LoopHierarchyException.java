package personia.hr.exception;

public class LoopHierarchyException extends RuntimeException {

    public LoopHierarchyException() {
        super("Input hierarchy contain loops");
    }
}
