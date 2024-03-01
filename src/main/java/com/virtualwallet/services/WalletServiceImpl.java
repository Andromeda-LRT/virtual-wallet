package com.virtualwallet.services;

import com.virtualwallet.exceptions.InsufficientFundsException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.model_mappers.CardMapper;
import com.virtualwallet.models.Card;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.model_dto.CardForAddingMoneyToWalletDto;
import com.virtualwallet.repositories.contracts.WalletRepository;
import com.virtualwallet.services.contracts.CardService;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.services.contracts.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.*;

@Service
public class WalletServiceImpl implements WalletService {

    private final Double MAX_TRANSACTION_AMOUNT = 10000.0;
    private final WalletRepository walletRepository;
    private final CardService cardService;
    private final WebClient webClient;
    private final UserService userService;
    private final CardMapper cardMapper;
    private final WalletToWalletTransactionService walletTransactionService;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository,
                             CardService cardService,
                             WebClient webClient,
                             UserService userService,
                             CardMapper cardMapper,
                             WalletToWalletTransactionService walletTransactionService) {
        this.walletRepository = walletRepository;
        this.cardService = cardService;
        this.webClient = webClient;
        this.userService = userService;
        this.walletTransactionService = walletTransactionService;
        this.cardMapper = cardMapper;
    }

//    @Override
//    public String addMoneyToWallet(User user, int card_id) {
//
//    }

    @Override
    public List<Wallet> getAllWallets(User user) {
        return walletRepository.getAllWallets(user);
    }

    @Override
    public Wallet getWalletById(User user, int wallet_id) {
        checkWalletOwnership(user, wallet_id);
        // todo check if user is part of this wallet - RENI
        //  checkUserPartOfWallet(user_id, wallet_id);
        return walletRepository.getById(wallet_id);
    }

    @Override
    public Wallet createWallet(User user, Wallet wallet) {
        wallet.setCreatedBy(user);
        return walletRepository.createWallet(wallet);
    }

    @Override
    public Wallet updateWallet(User user, Wallet wallet) {
        verifyWallet(wallet.getWalletId(), user);
        // if above method does not throw exception, then user is part of wallet
        // if above method throw exception then
        // checkUserPartOfWallet(user.getId(), wallet.getWalletId());
        // if this method does not throw exception, then user is part of wallet
        return walletRepository.updateWallet(wallet);
    }

    @Override
    public void delete(User user, int wallet_id) {
        verifyWallet(wallet_id, user);
        walletRepository.delete(user_id, wallet_id);
    }

    @Override
    public List<WalletToWalletTransaction> getAllWalletTransactions(User user, int wallet_id) {
        verifyWallet(wallet_id, user);
        return walletRepository.getAllWalletTransactions(wallet_id);
    }

    @Override
    public WalletToWalletTransaction getTransactionById(User user, int wallet_id, int transaction_id) {
        verifyWallet(wallet_id, user);
        return walletTransactionService.getWalletTransactionById(wallet_id, transaction_id);
    }


    //todo no need to implement for now
    // below method is initial version of walletToWalletTransaction to revert to it in case of issues.

    //  (User user, WalletToWalletTransaction transaction, int wallet_from_id, String iban_to) {
// walletToWalletTransaction(user, wallet_id, walletToWalletTransaction);
//    @Override
//    public void walletToWalletTransaction(User user, WalletToWalletTransaction transaction, int wallet_from_id, String iban_to) {
//        verifyWallet(wallet_from_id, user);
//
//        checkWalletBalance(wallet_from_id, transaction.getAmount()); // if wallet balance is less than transaction amount, throw exception
//        validateTransactionAmount(transaction); // if transaction amount is greater than 10000, status is pending
//        transaction.setTime(LocalDateTime.now());
//
//        if (!transaction.getStatus().equals("pending")) {
//            chargeWallet(wallet_from_id, transaction.getAmount()); // charge wallet
//            //set outgoing transaction properties
//            setOutgoingTransactionProperties(user.getId(), transaction, wallet_from_id, iban_to);
//            walletTransactionService.createTransaction(transaction);
//
//            //create incoming transaction
//            WalletToWalletTransaction walletToWalletTransactionIncoming = new WalletToWalletTransaction();
//            doIncomingTransaction(walletToWalletTransactionIncoming, transaction, iban_to);
//            walletTransactionService.createTransaction(walletToWalletTransactionIncoming);
//
//            // transfer money to recipient wallet
//            transferMoneyToRecipientWallet(iban_to, transaction.getAmount());
//        }
//    }

    @Override
    public void walletToWalletTransaction(User user, int wallet_from_id, WalletToWalletTransaction transaction) {
        verifyWallet(wallet_from_id, user);

        checkWalletBalance(wallet_from_id, transaction.getAmount()); // if wallet balance is less than transaction amount, throw exception
        validateTransactionAmount(transaction); // if transaction amount is greater than 10000, status is pending
        transaction.setTime(LocalDateTime.now());

        if (!transaction.getStatus().getName().equals("pending")) {
            chargeWallet(wallet_from_id, transaction.getAmount()); // charge wallet

            //set outgoing transaction type
            transaction.setTransactionTypeId(2);
            walletTransactionService.createTransaction(transaction);

            //create incoming transaction
            WalletToWalletTransaction walletToWalletTransactionIncoming = new WalletToWalletTransaction();
            doIncomingTransaction(walletToWalletTransactionIncoming, transaction);
            walletTransactionService.createTransaction(walletToWalletTransactionIncoming);

            // transfer money to recipient wallet
            transferMoneyToRecipientWallet(iban_to, transaction.getAmount());
        }
    }

//    @Override
//    public Transaction updateTransaction(User user, Transaction transaction, int wallet_id) {
//        verifyWallet(wallet_id, user);
//        validateTransactionAmount(transaction);
//        return transactionService.updateTransaction(user, transaction, wallet_id);
//    }

    @Override
    public void approveTransaction(User user, int transaction_id, int wallet_id) {
        // TODO To be implemented - TED
//        if (!user.getRole().equals("admin")) {
//            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
//        }
//        verifyWallet(wallet_id, user);
//        Transaction transaction = walletTransactionService.getTransactionById(transaction_id);
//        walletTransactionService.updateTransaction(transaction);
    }

    @Override
    public void cancelTransaction(User user, int transaction_id, int wallet_id) {
        // TODO To be implemented - TED
//        verifyWallet(wallet_id, user);
//        if (!user.getRole().equals("admin")) {
//            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
//        }
//        Transaction transaction = walletTransactionService.getTransactionById(transaction_id);
//        walletTransactionService.updateTransaction(transaction);
    }

    @Override
    public void transactionWithCard(User user, int card_id, int wallet_id) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = webClient.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(URI.create(DUMMY_API_COMPLETE_URL));
        CardForAddingMoneyToWalletDto cardDto = cardMapper.toDummyApiDto(cardService.getCard(card_id, user));
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(cardDto);
        WebClient.ResponseSpec responseSpec = populateResponseSpec(headersSpec);
        Mono<String> response = headersSpec.retrieve().bodyToMono(String.class);
        if (response.block().equals(APPROVED_TRANSFER)) {
            //todo add required amount to wallet
        }
        if (response.block().equals(DECLINED_TRANSFER)) {
            //todo send message to client that transaction was unsuccessful
        }
    }

//    private void checkUserPartOfWallet(int user_id, int wallet_id) {
//        // TODO this method should throw exception if user is not part of wallet - LYUBIMA
//        if (!walletRepository.checkIfUserIsPartOfWallet(user_id, wallet_id)) {
//            throw new UnauthorizedOperationException("You are not part of this wallet");
//        }
//    }

    private void checkWalletOwnership(User user, int wallet_id) {
        if (!walletRepository.checkWalletOwnership(user.getId(), wallet_id) == 1 && !userService.verifyAdminAccess(user)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }

    private void checkWalletExistence(int wallet_id) {
//        if (walletRepository.getWalletById(wallet_id) == null) {
//            throw new EntityNotFoundException("Wallet does not exist");
//        }
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

    private void verifyWallet(int wallet_id, User user) {
        checkWalletExistence(wallet_id);
        checkWalletOwnership(user, wallet_id);
    }

    //todo consider moving to WalletToWalletTransactionService
    private void validateTransactionAmount(WalletToWalletTransaction walletToWalletTransaction) {
        if (walletToWalletTransaction.getAmount() >= MAX_TRANSACTION_AMOUNT) {
            walletToWalletTransaction.setStatus("pending");
        }
        walletToWalletTransaction.setStatus("approved");
    }

    public Wallet checkIbanExistence(String ibanTo) {
        return walletRepository.getByStringField("iban", ibanTo);
    }

    private void checkWalletBalance(int walletFromId, double amount) {
        Wallet wallet = walletRepository.getById(walletFromId);
        if (wallet.getBalance() <= amount) {
            throw new InsufficientFundsException("wallet", wallet.getIban(), wallet.getBalance(), amount);
        }
    }

    private void chargeWallet(int walletFromId, double amount) {
        Wallet walletFrom = walletRepository.getById(walletFromId);
        double newBalance = walletFrom.getBalance() - amount;
        walletFrom.setBalance(newBalance);
        walletRepository.update(walletFrom);
    }

    private void doIncomingTransaction(WalletToWalletTransaction walletToWalletTransactionIncoming,
                                       WalletToWalletTransaction transactionFrom) {
        walletToWalletTransactionIncoming.setAmount(transactionFrom.getAmount());
        walletToWalletTransactionIncoming.setTime(LocalDateTime.now());
        walletToWalletTransactionIncoming.setTransactionTypeId(1);
        walletToWalletTransactionIncoming.setUserId(transactionFrom.getUserId());
        walletToWalletTransactionIncoming.setRecipientWalletId(walletRepository
                .getById(transactionFrom.getRecipientWalletId()).getWalletId());
    }

    private void transferMoneyToRecipientWallet(String ibanTo, double amount) {
        Wallet recipientWallet = walletRepository.getByStringField("iban", ibanTo);
        double newWalletBalance = recipientWallet.getBalance() + amount;
        recipientWallet.setBalance(newWalletBalance);
        walletRepository.update(recipientWallet);
    }
}
