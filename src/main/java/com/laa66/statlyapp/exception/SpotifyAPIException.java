package com.laa66.statlyapp.exception;

public class SpotifyAPIException extends RuntimeException {

    private int code;

    public SpotifyAPIException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
