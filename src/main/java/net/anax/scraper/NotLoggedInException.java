package net.anax.scraper;

public class NotLoggedInException extends RuntimeException{
    public NotLoggedInException(String message) {
        super(message);
    }
}
