package exceptions;

public class ConfigurationLoadException extends RuntimeException{
    /**
     * Constructor for ConfigurationLoadException.
     *
     * @param message Descriptive message to explain the error.
     * @param cause The throwable that caused this exception to get thrown.
     */
    public ConfigurationLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
