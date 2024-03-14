package com.virtualwallet.services;

import com.virtualwallet.exceptions.InsufficientFundsException;
import com.virtualwallet.exceptions.LimitReachedException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.exceptions.UnusedWalletBalanceException;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.model_mappers.CardMapper;
import com.virtualwallet.models.*;
import com.virtualwallet.models.input_model_dto.CardForAddingMoneyToWalletDto;
import com.virtualwallet.repositories.contracts.WalletRepository;
import com.virtualwallet.services.contracts.*;
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
import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final CardService cardService;
    private final WebClient webClient;
    private final UserService userService;
    private final CardMapper cardMapper;
    private final WalletTransactionService walletTransactionService;
    private final CardTransactionService cardTransactionService;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository,
                             CardService cardService,
                             WebClient webClient,
                             UserService userService,
                             CardMapper cardMapper,
                             WalletTransactionService walletTransactionService,
                             CardTransactionService cardTransactionService) {
        this.walletRepository = walletRepository;
        this.cardService = cardService;
        this.webClient = webClient;
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
    public List<User> getRecipient(UserModelFilterOptions userFilter) {
        return userService.getRecipient(userFilter);
    }

    @Override
    public Wallet getWalletById(User user, int wallet_id) {
        Wallet wallet = walletRepository.getById(wallet_id);
        checkWalletOwnership(user, wallet);

        // todo check if user is part of this wallet - RENI
        //  checkUserPartOfWallet(user_id, wallet_id);
        return wallet;
    }

    @Override
    public Wallet createWallet(User user, Wallet wallet) {
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
        if (walletToBeDeleted.getCreatedBy()!=user.getId()){
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR_GENERAL);
        }
        user.getWallets().remove(walletToBeDeleted);
        walletToBeDeleted.setArchived(true);
        walletRepository.update(walletToBeDeleted);
        userService.update(user, user);
    }

    @Override
    public List<WalletToWalletTransaction> getAllWalletTransactions(User user, int wallet_id) {
        Wallet wallet = verifyWallet(wallet_id, user);
        return walletTransactionService.getUserWalletTransactions(wallet);
    }

    @Override
    public List<WalletToWalletTransaction> getAllWalletTransactionsWithFilter
            (WalletTransactionModelFilterOptions transactionFilter, User user, int wallet_id) {
        Wallet wallet = verifyWallet(wallet_id, user);
        return walletTransactionService.getAllWalletTransactionsWithFilter(user, transactionFilter, wallet);
    }

    @Override
    public List<CardToWalletTransaction> getAllCardTransactions(User user, int wallet_id) {
        verifyWallet(wallet_id, user);
        return new ArrayList<>(walletRepository.getById(wallet_id).getCardTransactions());
    }

    @Override
    public List<CardToWalletTransaction> getAllCardTransactionsWithFilter(User user, CardTransactionModelFilterOptions transactionFilter) {
        return new ArrayList<>(cardTransactionService.getAllCardTransactionsWithFilter(user, transactionFilter));
    }
    @Override
    public WalletToWalletTransaction getTransactionById(User user, int wallet_id, int transaction_id) {
        // todo currently the method should be able to fetch the transaction by just having its id
        verifyWallet(wallet_id, user);
        return walletTransactionService.getWalletTransactionById(transaction_id);
    }

    @Override
    public void walletToWalletTransaction(User user, int senderWalletId, WalletToWalletTransaction transaction) {
        Wallet senderWallet = verifyWallet(senderWalletId, user);
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
        } else if (responseResult.equals(DECLINED_TRANSFER)){
            cardTransactionService.declineTransaction(cardTransaction, user, card, wallet);
            walletRepository.update(wallet);
        }
        return cardTransaction;
    }

    @Override
    public Wallet getByStringField(String id, String s) {
        return walletRepository.getByStringField(id, s);
    }

    private void checkWalletOwnership(User user, Wallet wallet) {
        if (!walletRepository.checkWalletOwnership(user.getId(), wallet.getWalletId()) && !userService.verifyAdminAccess(user)) {
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
        checkWalletOwnership(user, wallet);
        return wallet;
    }

    private void verifyCard(int cardId, User user) {
        cardService.verifyCardExistence(cardId);
        cardService.authorizeCardAccess(cardId, user);
    }

    @Override
    public Wallet checkIbanExistence(String ibanTo) {
        return walletRepository.getByStringField("iban", ibanTo);
    }

    @Override
    public void checkWalletBalance(Wallet wallet, double amount) {
        if (wallet.getBalance() <= amount) {
            throw new InsufficientFundsException("wallet", wallet.getIban(), wallet.getBalance(), amount);
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
    public void addUserToWallet(User user, int wallet_id, int user_id){
    Wallet wallet = getWalletById(user, wallet_id);
    if (wallet.getWalletTypeId() == 1 || wallet.getCreatedBy()!=user.getId()) {
        throw new UnauthorizedOperationException(PERMISSIONS_ERROR_GENERAL);
    }
    if (walletRepository.getWalletUsers(wallet_id).size()>=5){
        throw new LimitReachedException(ACCOUNTS_LIMIT_REACHED);
    }
        UserWallets userWallets = new UserWallets(userService.verifyUserExistence(user_id), wallet);
    walletRepository.addUserToWallet(userWallets);
    }

    @Override
    public void removeUserFromWallet(User user, int wallet_id, int user_id){
        Wallet wallet = getWalletById(user, wallet_id);
        if (wallet.getWalletTypeId() == 1 || wallet.getCreatedBy()!=user.getId()) {
            throw new UnauthorizedOperationException(PERMISSIONS_ERROR_GENERAL);
        }

        UserWallets userWallets = new UserWallets(userService.verifyUserExistence(user_id), wallet);
        walletRepository.removeUserFromWallet(userWallets);
    }

    @Override
    public List<User> getWalletUsers(User user, int wallet_id){
        Wallet wallet = getWalletById(user, wallet_id);
        if (walletRepository.getWalletUsers(wallet_id)
                .stream().anyMatch(user1 -> user1.getId() == user.getId())) {
            return walletRepository.getWalletUsers(wallet_id);
        }
        throw new UnauthorizedOperationException(PERMISSIONS_ERROR_GENERAL);
    }

    private String sendTransferRequest(Card card) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = webClient.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(URI.create(DUMMY_API_COMPLETE_URL));
        CardForAddingMoneyToWalletDto cardDto = cardMapper.toDummyApiDto(card);
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(cardDto);
        WebClient.ResponseSpec responseSpec = populateResponseSpec(headersSpec);
        Mono<String> response = headersSpec.retrieve().bodyToMono(String.class);
        return response.block();
    }
}
