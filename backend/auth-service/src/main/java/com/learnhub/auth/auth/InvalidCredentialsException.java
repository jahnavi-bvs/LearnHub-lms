package com.learnhub.auth.auth;

/**
 * Same message whether the email is unknown or the password is wrong, so an attacker
 * can't use the login form to discover which emails have accounts.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
