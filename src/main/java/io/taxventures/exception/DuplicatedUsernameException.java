package io.taxventures.exception;

public class DuplicatedUsernameException extends ApplicationException{
    public DuplicatedUsernameException() {
        super("user with provided email already exists!");
    }
}
