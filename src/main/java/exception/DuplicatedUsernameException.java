package exception;

public class DuplicatedUsernameException extends ApplicationException{
    public DuplicatedUsernameException() {
        super("user with provided username already exists!");
    }
}
