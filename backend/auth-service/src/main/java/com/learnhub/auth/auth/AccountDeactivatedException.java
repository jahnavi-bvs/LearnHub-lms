package com.learnhub.auth.auth;

/** The password was right, but an admin has deactivated this account. */
public class AccountDeactivatedException extends RuntimeException {

    public AccountDeactivatedException() {
        super("This account has been deactivated. Please contact an administrator.");
    }
}
