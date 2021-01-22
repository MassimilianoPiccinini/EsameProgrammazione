package Exceptions;

public class WrongDateException extends Exception{

    private static final long serialVersionUID = 1L;

    public WrongDateException(String message) {
        super(message);

    }
}