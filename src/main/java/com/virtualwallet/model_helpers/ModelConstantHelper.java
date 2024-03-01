package com.virtualwallet.model_helpers;

public class ModelConstantHelper {

    public static final String INVALID_EMAIL_ERROR_MESSAGE = "Please enter a valid email address";

    public static final String USERNAME_ERROR_MESSAGE = "Username must be of between 2 to 20 symbols long" +
            " with no special characters";
    public static final String PASSWORD_ERROR_MESSAGE = "Password must be at least 8 and up to 20 symbols long, " +
            "containing at least 1 uppercase, 1 lowercase, 1 special character and 1 digit ";
    public static final String EMPTY_ERROR_MESSAGE = "Field can't be empty";

    public static final String INVALID_PHONE_NUMBER_ERROR_MESSAGE = "Phone number must be 10 digits long";

    public static final String NAME_ERROR_MESSAGE = "Field should be between 3 and 20 symbols.";
    public static final String UNAUTHORIZED_OPERATION_ERROR_MESSAGE = "You are not authorized to perform this operation";

    public static final String AUTHORIZATION_HEADER_NAME =
            "Authorization";
    public static final String THE_REQUEST_RESOURCE_REQUIRES_AUTHENTICATION =
            "The requested resource requires authentication.";
    public static final String INVALID_AUTHENTICATION =
            "Invalid authentication";
    public static final String WRONG_USERNAME_OR_PASSWORD = "Wrong username or password.";
    public static final String PERMISSIONS_ERROR =
            "You don't have the permissions to update this user information.";
    public static final String WALLET_NOT_FOUND_ERROR_MESSAGE = "Wallet not found";
}
