package personia.hr.exception;

public class MultipleRootFoundException extends RuntimeException {

    public MultipleRootFoundException() {
        super("Multiple roots found");
    }
}
