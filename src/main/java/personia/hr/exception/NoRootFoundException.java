package personia.hr.exception;

public class NoRootFoundException extends RuntimeException {

    public NoRootFoundException() {
        super("No root supervisor found");
    }
}
