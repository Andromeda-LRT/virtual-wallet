package com.virtualwallet.services;

import com.virtualwallet.exceptions.*;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.model_mappers.CardMapper;
import com.virtualwallet.models.*;
import com.virtualwallet.models.input_model_dto.CardForAddingMoneyToWalletDto;
import com.virtualwallet.repositories.contracts.WalletRepository;
import com.virtualwallet.services.contracts.*;
import com.virtualwallet.utils.UtilHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final CardService cardService;
    private final WebClient dummyApiWebClient;
    private final UserService userService;
    private final CardMapper cardMapper;
    private final WalletTransactionService walletTransactionService;
    private final CardTransactionService cardTransactionService;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository,
                             CardService cardService,
                             WebClient dummyApiWebClient,
                             UserService userService,
                             CardMapper cardMapper,
                             WalletTransactionService walletTransactionService,
                             CardTransactionService cardTransactionService) {
        this.walletRepository = walletRepository;
        this.cardService = cardService;
        this.dummyApiWebClient = dummyApiWebClient;
        this.userService = userService;
        this.cardMapper = cardMapper;
        this.walletTransactionService = walletTransactionService;
        this.cardTransactionService = cardTransactionService;
    }

    @Override
    public List<Wallet> getAllWallets(User user) {
        return walletRepository.getAllWallets(user);
    }


    @Override
    public List<Wallet> getAllPersonalWallets(User user) {
//       return getAllWallets(user).stream().filter(wallet -> wallet.getWalletTypeId() == 1).toList();
        return userService.get(user.getId(), user).getWallets()
                .stream()
                .filter(wallet -> wallet.getWalletTypeId() == WALLET_TYPE_ID_1)
                .toList();
    }

    @Override
    public List<Wallet> getAllJoinWallets(User user) {
//        return getAllWallets(user).stream().filter(wallet -> wallet.getWalletTypeId() == 2).toList();
        return userService.get(user.getId(), user).getWallets()
                .stream()
                .filter(wallet -> wallet.getWalletTypeId() == WALLET_TYPE_ID_2)
                .toList();
    }


    @Override
    public List<User> getRecipient(UserModelFilterOptions userFilter) {
        return userService.getRecipient(userFilter);
    }

    @Override
    public Wallet getWalletById(User user, int wallet_id) {
        //verifyIfUserIsPartOfWallet(user, wallet_id);
        checkWalletOwnership(user, wallet_id);

        return walletRepository.getById(wallet_id);
    }

    @Override
    public Wallet createWallet(User user, Wallet wallet) {
        restrictUserPersonalWallets(user, wallet);
        checkIfWalletNameExistsInUserList(wallet.getName(), user);
        wallet.setCreatedBy(user.getId());
        walletRepository.create(wallet);
        user.getWallets().add(wallet);
        userService.update(user, user);
        return wallet;
    }
    @Override
    public Wallet updateWallet(User user, Wallet wallet) {
        verifyWallet(wallet.getWalletId(), user);
        // if above method does not throw exception, then user is part of wallet
        // if above method throw exception then
        // checkUserPartOfWallet(user.getId(), wallet.getWalletId());
        // if this method does not throw exception, then user is part of wallet
        walletRepository.update(wallet);
        return wallet;
    }

    @Override
    public void delete(User user, int wallet_id) {

        Wallet walletToBeDeleted = verifyWallet(wallet_id, user);
        if (walletToBeDeleted.getBalance() > 0) {
            throw new UnusedWalletBalanceException(String.valueOf(walletToBeDeleted.getBalance()));
        }
        if (walletToBeDeleted.getCreatedBy() != user.getId()) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR_GENERAL);
        }
        user.getWallets().remove(walletToBeDeleted);
        walletToBeDeleted.setArchived(true);
        walletRepository.update(walletToBeDeleted);
        userService.update(user, user);
    }

    @Override
    public List<WalletToWalletTransaction> getUserWalletTransactions(WalletTransactionModelFilterOptions transactionFilter, User user, int wallet_id) {
        checkWalletOwnership(user, wallet_id);
        return walletTransactionService.getUserWalletTransactions(user, transactionFilter, wallet_id);
    }

    @Override
    public List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter
            (WalletTransactionModelFilterOptions transactionFilter, User user, int wallet_id) {
        verifyWallet(wallet_id, user);
        return walletTransactionService.getAllWalletTransactionsWithFilter(user, transactionFilter);
    }

    @Override
    public List<CardToWalletTransaction> getAllCardTransactionsWithFilter(User user, CardTransactionModelFilterOptions transactionFilter) {
        return new ArrayList<>(cardTransactionService.getAllCardTransactionsWithFilter(user, transactionFilter));
    }

    @Override
    public List<CardToWalletTransaction> getUserCardTransactions(int walletId, User user, CardTransactionModelFilterOptions transactionFilter) {
        return new ArrayList<>(cardTransactionService.getUserCardTransactions(walletId, user, transactionFilter));
    }

    @Override
    public WalletToWalletTransaction getTransactionById(User user, int wallet_id, int transaction_id) {
        // todo currently the method should be able to fetch the transaction by just having its id
        checkWalletOwnership(user, wallet_id);
        return walletTransactionService.getWalletTransactionById(transaction_id);
    }

    @Override
    public void walletToWalletTransaction(User user, int senderWalletId, WalletToWalletTransaction transaction) {
        userService.isUserBlocked(user);

        Wallet senderWallet = verifyWallet(senderWalletId, user);
        Wallet senderWallet = getWalletById(user, senderWalletId);
        Wallet recipientWallet = walletRepository.getById(transaction.getRecipientWalletId());
        // if wallet balance is less than transaction amount, throw exception
        checkWalletBalance(senderWallet, transaction.getAmount());
        if (walletTransactionService.createWalletTransaction(user, transaction, senderWallet, recipientWallet)) {
            chargeWallet(senderWallet, transaction.getAmount());
            // transfer money to recipient wallet
            transferMoneyToRecipientWallet(recipientWallet, transaction.getAmount());
//            walletRepository.update(senderWallet);
//            walletRepository.update(recipientWallet);
        } else {
            walletRepository.update(senderWallet);
        }
    }
    //todo discuss if this method is necessary or can be delete safely
//    @Override
//    public Transaction updateTransaction(User user, Transaction transaction, int wallet_id) {
//        verifyWallet(wallet_id, user);
//        validateTransactionAmount(transaction);
//        return transactionService.updateTransaction(user, transaction, wallet_id);
//    }

//    @Override
//    public void approveTransaction(User user, int transaction_id, int wallet_id) {
//        // TODO To be implemented - TED consider using a transactionObj since
//        //  a transaction will be already have been created at this point or alternatively just its id
//        WalletToWalletTransaction transactionToBeApproved =
//                walletTransactionService.getWalletTransactionById(transaction_id);
//        Wallet senderWallet;
//        Wallet recipientWallet;
//        try {
//            senderWallet = checkWalletExistence(wallet_id);
//            recipientWallet = getWalletById(user, transactionToBeApproved.getRecipientWalletId());
//            checkWalletBalance(senderWallet, transactionToBeApproved.getAmount());
//            walletTransactionService.approveTransaction(transactionToBeApproved, recipientWallet);
//            chargeWallet(senderWallet, transactionToBeApproved.getAmount());
//            transferMoneyToRecipientWallet(recipientWallet, transactionToBeApproved.getAmount());
//            //   walletRepository.update(recipientWallet);
//        } catch (InsufficientFundsException e) {
//            walletTransactionService.cancelTransaction(transactionToBeApproved);
//        }
//    }
//
//    @Override
//    public void cancelTransaction(User user, int transaction_id, int wallet_id) {
//        // TODO To be implemented - TED consider using a transactionObj since
//        //  a transaction will be already have been created at this point or alternatively just its id
//
//        WalletToWalletTransaction transactionToBeCancelled =
//                walletTransactionService.getWalletTransactionById(transaction_id);
//
//        walletTransactionService.cancelTransaction(transactionToBeCancelled);
//    }

    @Override
    public CardToWalletTransaction transactionWithCard(User user, int card_id, int wallet_id,
                                                       CardToWalletTransaction cardTransaction) {
        Wallet wallet = getWalletById(user, wallet_id);
        Card card = cardService.getCard(card_id, user, user.getId());
        String responseResult = sendTransferRequest(card);
        if (responseResult.equals(APPROVED_TRANSFER)) {
            wallet.setBalance(cardTransaction.getAmount() + wallet.getBalance());
            cardTransactionService.approveTransaction(cardTransaction, user, card, wallet);
            walletRepository.update(wallet);
        } else if (responseResult.equals(DECLINED_TRANSFER)) {
            cardTransactionService.declineTransaction(cardTransaction, user, card, wallet);
            walletRepository.update(wallet);
        }
        return cardTransaction;
    }

    @Override
    public Wallet getByStringField(String id, String s) {
        return walletRepository.getByStringField(id, s);
    }

    @Override
    public Wallet checkIbanExistence(String ibanTo) {
        return walletRepository.getByStringField("iban", ibanTo);
    }

    @Override
    public void checkWalletBalance(Wallet wallet, double amount) {
        if (wallet.getBalance() <= amount) {
            throw new InsufficientFundsException(
                    "wallet", wallet.getIban(),
                    wallet.getBalance(), amount
            );
        }
    }

    @Override
    public void chargeWallet(Wallet wallet, double amount) {
        double newBalance = wallet.getBalance() - amount;
        wallet.setBalance(newBalance);
        walletRepository.update(wallet);
    }

    @Override
    public void transferMoneyToRecipientWallet(Wallet recipientWallet, double amount) {
        double newWalletBalance = recipientWallet.getBalance() + amount;
        recipientWallet.setBalance(newWalletBalance);
        walletRepository.update(recipientWallet);
    }

    @Override
    public void addUserToWallet(User user, int wallet_id, int user_id) {
        Wallet wallet = getWalletById(user, wallet_id);
        if (wallet.getWalletTypeId() == 1 || wallet.getCreatedBy() != user.getId()) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR_GENERAL);
        }
        if (walletRepository.getWalletUsers(wallet_id).size() >= 5) {
            throw new LimitReachedException(ACCOUNTS_LIMIT_REACHED);
        }
        UserWallets userWallets = new UserWallets(userService.verifyUserExistence(user_id), wallet);
        walletRepository.addUserToWallet(userWallets);
    }

    @Override
    public void removeUserFromWallet(User user, int wallet_id, int user_id) {
        Wallet wallet = getWalletById(user, wallet_id);
        if (wallet.getWalletTypeId() == 1 || wallet.getCreatedBy() != user.getId()) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR_GENERAL);
        }

        UserWallets userWallets = new UserWallets(userService.verifyUserExistence(user_id), wallet);
        walletRepository.removeUserFromWallet(userWallets);
    }

    @Override
    public List<User> getWalletUsers(User user, int wallet_id) {
//        Wallet wallet = getWalletById(user, wallet_id);
//        if (walletRepository.getWalletUsers(wallet_id)
//                .stream().anyMatch(user1 -> user1.getId() == user.getId())) {
//            return walletRepository.getWalletUsers(wallet_id);
//        }
        List<User> walletUsers = walletRepository.getWalletUsers(wallet_id);
        if (!walletUsers.contains(user)) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR_GENERAL);
        }
        return walletUsers;
    }
//        //todo remove and use checkWalletOwnership instead
//    @Override
//    public boolean verifyIfUserIsPartOfWallet(User user, int wallet_id) {
//        if (walletRepository.getWalletUsers(wallet_id)
//                .stream().noneMatch(user1 -> user1.getId() == user.getId())) {
//            throw new UnauthorizedOperationException(PERMISSIONS_ERROR_GENERAL);
//        }
//        return true;
//    }

    @Override
    public boolean verifyIfUserIsWalletOwner(User user, Wallet wallet) {
        return wallet.getCreatedBy() == user.getId();
    }

    /**
     * @param wallet
     * @param user
     * @return returns a Boolean depending on whether the
     * current Wallet obj is created by provided User
     * @throws com.virtualwallet.exceptions.UnauthorizedOperationException if User is not owner of wallet
     */

    @Override
    public boolean verifyIfUserIsWalletOwner(Wallet wallet, User user) {
        if (wallet.getCreatedBy() != user.getId()) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
        return true;
    }

    @Override
    public void checkWalletOwnership(User user, int wallet_id) {
        if (!walletRepository.checkWalletOwnership(user.getId(), wallet_id)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }

    private Wallet checkWalletExistence(int wallet_id) {
        return walletRepository.getById(wallet_id);
    }

    private WebClient.ResponseSpec populateResponseSpec(WebClient.RequestHeadersSpec<?> headersSpec) {
        return headersSpec.header(
                        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .acceptCharset(StandardCharsets.UTF_8)
                .ifNoneMatch("*")
                .ifModifiedSince(ZonedDateTime.now())
                .retrieve();
    }

    private Wallet verifyWallet(int wallet_id, User user) {
        Wallet wallet;
        wallet = checkWalletExistence(wallet_id);
        verifyIfUserIsWalletOwner(wallet, user);
        return wallet;
    }

    private void verifyCard(int cardId, User user) {
        cardService.verifyCardExistence(cardId);
        cardService.authorizeCardAccess(cardId, user);
    }

    private String sendTransferRequest(Card card) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = dummyApiWebClient.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(URI.create(DUMMY_API_COMPLETE_URL));
        CardForAddingMoneyToWalletDto cardDto = cardMapper.toDummyApiDto(card);
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(cardDto);
        WebClient.ResponseSpec responseSpec = populateResponseSpec(headersSpec);
        Mono<String> response = headersSpec.retrieve().bodyToMono(String.class);
        return response.block();
    }

    private void checkIfWalletNameExistsInUserList (String walletName, User user){
        if (user.getWallets().stream().anyMatch(wallet -> wallet.getName().equals(walletName))) {
            throw new DuplicateEntityException("Wallet", "wallet name", walletName);
        }
    }
    private void restrictUserPersonalWallets (User user, Wallet wallet){
        if (user.getWallets().size() == 4 && wallet.getWalletTypeId() == 1) {
            throw new LimitReachedException(ACCOUNTS_LIMIT_REACHED);
        }
    }
}
