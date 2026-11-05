/**
 * 
 */
package it.unicam.cs.asdl.essortingsol;

/**
 * 
 * Eccezione che segnala che un algoritmo di ordinamento ha commesso un errore.
 * 
 * @author Luca Tesei
 *
 */
public class SortingException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 7289728998235L;

    /**
     * 
     */
    public SortingException() {
        super();
        // Costruttore delegato alla superclasse RuntimeException.
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public SortingException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        // Costruttore delegato alla superclasse RuntimeException.
    }

    /**
     * @param message
     * @param cause
     */
    public SortingException(String message, Throwable cause) {
        super(message, cause);
        // Costruttore delegato alla superclasse RuntimeException.
    }

    /**
     * @param message
     */
    public SortingException(String message) {
        super(message);
        // Costruttore delegato alla superclasse RuntimeException.
    }

    /**
     * @param cause
     */
    public SortingException(Throwable cause) {
        super(cause);
        // Costruttore delegato alla superclasse RuntimeException.
    }

}
