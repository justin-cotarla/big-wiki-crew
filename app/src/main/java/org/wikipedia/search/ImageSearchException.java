package org.wikipedia.search;

public class ImageSearchException extends Exception {
    public ImageSearchException(String message, Exception e) {
        super(message, e);
    }
}
