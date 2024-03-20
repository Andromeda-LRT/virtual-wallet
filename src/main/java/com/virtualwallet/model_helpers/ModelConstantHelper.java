package com.virtualwallet.model_helpers;

public class ModelConstantHelper {

    public static final String INVALID_EMAIL_ERROR_MESSAGE = "Please enter a valid email address";

    public static final String USERNAME_ERROR_MESSAGE = "Username must be of between 2 to 20 symbols long"
            + " with no special characters";
    public static final String PASSWORD_ERROR_MESSAGE
            = "Password must be at least 8 and up to 20 symbols long, " +
            "containing at least 1 uppercase, 1 lowercase, 1 special character and 1 digit ";
    public static final String EMPTY_ERROR_MESSAGE = "Field can't be empty";

    public static final String INVALID_PHONE_NUMBER_ERROR_MESSAGE = "Phone number must be 10 digits long";

    public static final String NAME_ERROR_MESSAGE = "Field should be between 3 and 20 symbols.";

    public static final String UNAUTHORIZED_OPERATION_ERROR_MESSAGE = "You are not authorized to perform this operation";

    public static final String INVALID_AUTHENTICATION = "Invalid authentication";

    public static final String WRONG_USERNAME_OR_PASSWORD = "Wrong username or password.";

    public static final String PERMISSIONS_ERROR = "You don't have the permissions to update this user information.";

    public static final String EXPIRED_CARD_ERROR_MESSAGE = "Card has expired";

    public static final String NOT_FOUND_CARD_ERROR_MESSAGE = "Card not found";

    public static final String INSUFFICIENT_FUNDS_ERROR_MESSAGE = "%s, with iban %s requires %.2f" + " more funds to complete the transaction.";

    public static final String THE_REQUESTED_RESOURCE_REQUIRES_AUTHENTICATION = "The requested resource requires authentication.";

    public static final String DUMMY_API_COMPLETE_URL = "http://localhost:8081/api/bank/withdrawal";

    public static final Double MAX_TRANSACTION_AMOUNT = 10000.0;

    public static final String APPROVED_TRANSFER = "Approve";

    public static final String DECLINED_TRANSFER = "Decline";

    public static final int INCOMING_TRANSACTION_TYPE_ID = 1;

    public static final int OUTGOING_TRANSACTION_TYPE_ID = 2;

    public static final int CONFIRMED_TRANSACTION_ID = 1;

    public static final int PENDING_TRANSACTION_ID = 2;

    public static final int DECLINED_TRANSACTION_ID = 3;

    public static final int USER_ROLE_ID = 1;

    public static final int ADMIN_ROLE_ID = 2;

    public static final String USER_USERNAME = "username";

    public static final String USER_EMAIL = "email";

    public static final String USER_PHONE_NUMBER = "phoneNumber";

    public static final String VALID_NUMBER = "Input must be a valid number.";

    public static final String TRANSACTION_PENDING_STATUS = "Pending";

    public static final String PROCESSED_TRANSACTION_MESSAGE = "Transaction is already processed";

    public static final String AUTHORIZATION = "Authorization";

    public static final String PERMISSIONS_ERROR_GENERAL = "You don't have the permissions to perform this operation";

    public static final String IMGBB_IMG_UPLOAD_ENDPOINT_URL = "https://api.imgbb.com/1/upload";

    public static final String REQUEST_KEY_IMAGE = "image";

    public static final String UPLOAD_IMG_API_REQUEST_KEY = "key";

    public static final int RESPONSE_STATUS_OK = 200;

    public static final String ACCOUNTS_LIMIT_REACHED = "The limits for users in a Joined account is 5.";

    public static final String PERSONAL_ACCOUNTS_LIMIT_REACHED = "The limits for personal wallets is 5.";

    public static final String DEFAULT_PROFILE_IMAGE
            = "https://www.pngall.com/wp-content/uploads/5/User-Profile-PNG-Image.png";
    public static final String HIDDEN_CARD_DIGITS = "****";

    public static final int CARD_LAST_4_DIGIT_START_INDEX = 12;

    public static final int CARD_LAST_4_DIGIT_LAST_INDEX = 16;

    public static final String WALLET_TYPE_1 = "Personal";

    public static final String WALLET_TYPE_2 = "Joined";

    public static final String EMPTY = "";

    public static final int WALLET_TYPE_ID_1 = 1;

    public static final int WALLET_TYPE_ID_2 = 2;

    public static final String DUPLICATE_WALLETUSER_ERROR_MESSAGE =
            "User with username %s is a member of the current Joined Wallet";

    public static final String INCOMING_TRANSACTION = "Incoming";

    public static final String OUTGOING_TRANSACTION = "Outgoing";

}
