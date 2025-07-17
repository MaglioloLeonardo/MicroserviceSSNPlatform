package tassproject.pharmaservice;

/**
 * Eccezione lanciata quando non si trova una risorsa.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s non trovato con id %s", resourceName, id));
    }

}
