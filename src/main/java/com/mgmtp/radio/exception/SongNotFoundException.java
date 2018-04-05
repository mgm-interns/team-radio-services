package com.mgmtp.radio.exception;

public class SongNotFoundException extends RadioNotFoundException {

    public SongNotFoundException() {
        super("Can not find this song.");
    }

    public SongNotFoundException(String songId) {
        super("Can not find song " + songId + ".");
    }
}
