package co.nequi.franquicias_api_hexagonal.infrastructure.adapters.persistenceadapter.dynamo.exception;

public class DataAlreadyExists extends RuntimeException {
    public DataAlreadyExists(String message) {
        super(message);
    }
}
