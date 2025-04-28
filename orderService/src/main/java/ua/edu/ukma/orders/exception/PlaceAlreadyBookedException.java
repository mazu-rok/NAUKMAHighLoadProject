package ua.edu.ukma.orders.exception;

public class PlaceAlreadyBookedException extends RuntimeException {
    public PlaceAlreadyBookedException(String message) {
        super(message);
    }
}
