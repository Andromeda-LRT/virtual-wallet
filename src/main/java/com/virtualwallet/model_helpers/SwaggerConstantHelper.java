package com.virtualwallet.model_helpers;

public class SwaggerConstantHelper {
    public static final String ONLY_BY_ADMINS = " This operation can be done only by Admins.";

    //----------------------USER CONTROLLER------------------------------------

    public static final String GET_ALL_USERS_SUMMARY = "Get all users";
    public static final String GET_ALL_USERS_DESCRIPTION = "Returns all registered users.";
    public static final String GET_USER_BY_ID_SUMMARY = "Get user by ID";
    public static final String GET_USER_BY_ID_DESCRIPTION = "Returns a user by searching by its ID.";
    public static final String GET_ALL_USER_CARDS_SUMMARY = "Get all user cards";
    public static final String GET_ALL_USER_CARDS_DESCRIPTION = "Returns all the cards that the user has registered.";
    public static final String GET_USER_CARD_SUMMARY = "Get user card by card id";
    public static final String GET_USER_CARD_DESCRIPTION = "Returns a user card by the card id.";
    public static final String CREATE_USER_SUMMARY = "Create a new user";
    public static final String CREATE_USER_DESCRIPTION = "Registers a new user when valid details are provided.";
    public static final String CREATE_USER_CARD_SUMMARY = "Create a card";
    public static final String CREATE_USER_CARD_DESCRIPTION = "Adds a new card to the users account.";
    public static final String UPDATE_USER_SUMMARY = "Update user";
    public static final String UPDATE_USER_DESCRIPTION = "Updates the details of a user.";
    public static final String BLOCK_USER_SUMMARY = "Block user";
    public static final String BLOCK_USER_DESCRIPTION = "Block user profile.";
    public static final String UNBLOCK_USER_SUMMARY = "Unblock user";
    public static final String UNBLOCK_USER_DESCRIPTION = "Unblock user profile.";
    public static final String UPDATE_USER_CARD_SUMMARY = "Update Card";
    public static final String UPDATE_USER_CARD_DESCRIPTION = "Updates a registered card.";
    public static final String DELETE_USER_SUMMARY = "Delete user";
    public static final String DELETE_USER_DESCRIPTION = "Deletes user profile.";
    public static final String DELETE_USER_CARD_SUMMARY = "Delete user card";
    public static final String DELETE_USER_CARD_DESCRIPTION = "Deletes a user card";
    public static final String GIVE_ADMIN_RIGHTS_SUMMARY = "Give user admin rights";
    public static final String GIVE_ADMIN_RIGHTS_DESCRIPTION = "Changes the role of a simple user to admin";
    public static final String REMOVE_ADMIN_RIGHTS_SUMMARY = "Remove admin rights";
    public static final String REMOVE_ADMIN_RIGHTS_DESCRIPTION = "Changes the role an admin to a simple user";

    //----------------------WALLET CONTROLLER------------------------------------

    public static final String GET_ALL_WALLETS_SUMMARY = "Get all wallets";
    public static final String GET_ALL_WALLETS_DESCRIPTION = "Get all wallets for a certain user.";
    public static final String GET_WALLET_BY_ID_SUMMARY = "Get wallet by id";
    public static final String GET_WALLET_BY_ID_DESCRIPTION = "Get all wallets by id.";
    public static final String CREATE_WALLET_SUMMARY = "Create wallet";
    public static final String CREATE_WALLET_DESCRIPTION = "Create a wallet.";
    public static final String UPDATE_WALLET_SUMMARY = "Update wallet";
    public static final String UPDATE_WALLET_DESCRIPTION = "Update existing wallet.";
    public static final String DELETE_WALLET_SUMMARY = "Delete wallet";
    public static final String DELETE_WALLET_DESCRIPTION = "Delete existing wallet.";
    public static final String GET_WALLET_TRANSACTIONS_SUMMARY = "Get wallet history.";
    public static final String GET_WALLET_TRANSACTIONS_DESCRIPTION = "Get the history of the wallet with available filtration and sorting.";
    public static final String GET_CARD_WALLET_TRANSACTIONS_SUMMARY = "Get wallet card transactions.";
    public static final String GET_CARD_WALLET_TRANSACTIONS_DESCRIPTION = "Get the history of the wallet card transactions with available filtration and sorting.";
    public static final String GET_TRANSACTION_BY_ID_SUMMARY = "Get wallet transaction by id";
    public static final String GET_TRANSACTION_BY_ID_DESCRIPTION = "Get wallet transaction by id.";
    public static final String CREATE_TRANSACTION_SUMMARY = "Create wallet transaction";
    public static final String CREATE_TRANSACTION_DESCRIPTION = "Create a new wallet transaction.";
    public static final String CREATE_TRANSACTION_WITH_CARD_SUMMARY = "Create card transaction";
    public static final String CREATE_TRANSACTION_WITH_CARD_DESCRIPTION = "Create a new card transaction.";
    public static final String GET_RECIPIENT_SUMMARY = "Get recipient.";
    public static final String GET_RECIPIENT_DESCRIPTION = "Get recipient with available filtration and sorting.";
    public static final String ADD_USER_TO_WALLET_SUMMARY = "Add user to joined wallet";
    public static final String ADD_USER_TO_WALLET_DESCRIPTION = "Add user to joined wallet. Only the creator of the wallet can add users to it.";
    public static final String REMOVE_USER_FROM_WALLET_SUMMARY = "Remove user from joined wallet";
    public static final String REMOVE_USER_FROM_WALLET_DESCRIPTION = "Remove user from joined wallet. Only the creator of the wallet can remove users from it.";
    public static final String GET_ALL_WALLET_USERS_SUMMARY = "Get all join wallet users";
    public static final String GET_ALL_WALLET_USERS_DESCRIPTION = "Get all join wallet users.";

    //----------------------TRANSACTION CONTROLLER------------------------------------

    public static final String GET_ALL_TRANSACTION_SUMMARY = "Get all wallet transactions";
    public static final String GET_ALL_TRANSACTION_DESCRIPTION = "Get all wallet transactions for all users. This can be done only by admins.";
    public static final String GET_ALL_CARD_TRANSACTIONS_SUMMARY = "Get all card transactions";
    public static final String GET_ALL_CARD_TRANSACTIONS_DESCRIPTION = "Get all card transactions for all users. This can be done only by admins.";
    public static final String APPROVE_TRANSACTION_SUMMARY = "Approve pending transaction";
    public static final String APPROVE_TRANSACTION_DESCRIPTION = "Approve pending transaction. This can be done only by admins.";
    public static final String CANCEL_TRANSACTION_SUMMARY = "Cancel pending transaction";
    public static final String CANCEL_TRANSACTION_DESCRIPTION = "Cancel pending transaction. This can be done only by admins.";


}
