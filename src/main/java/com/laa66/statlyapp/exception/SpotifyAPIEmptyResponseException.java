package com.laa66.statlyapp.exception;

public class SpotifyAPIEmptyResponseException extends SpotifyAPIException {

    public SpotifyAPIEmptyResponseException(String message, int code) {
        super(message, code);
    }
}
