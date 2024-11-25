package com.stash.stash.exceptions;

public class UserNotAuthenticated extends Exception{
    public UserNotAuthenticated(String message) {
        super(message);
    }
}
