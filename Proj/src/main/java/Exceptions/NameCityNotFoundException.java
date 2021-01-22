package Exceptions;

public class NameCityNotFoundException extends Exception{

    private static final long serialVersionUID = 1L;

    public NameCityNotFoundException(String message) {
        super(message);
        
    }
}