package com.virtualwallet.services;

import com.virtualwallet.exceptions.InsufficientFundsException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.models.*;
import com.virtualwallet.model_mappers.CardMapper;
import com.virtualwallet.models.model_dto.CardForAddingMoneyToWalletDto;
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
        return walletTransactionService.getAllWalletTransactions();
    }

    @Override
    public WalletToWalletTransaction getTransactionById(User user, int wallet_id, int transaction_id) {
        // todo currently the method should be able to fetch the transaction by just having its id
        verifyWallet(wallet_id, user);
        return walletTransactionService.getWalletTransactionById(transaction_id);
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
    public void walletToWalletTransaction(User user, int senderWalletId, WalletToWalletTransaction transaction) {
        Wallet senderWallet = verifyWallet(senderWalletId, user);
        // if wallet balance is less than transaction amount, throw exception
        checkWalletBalance(senderWallet, transaction.getAmount());
        if (walletTransactionService.createWalletTransaction(user, transaction)) {
            chargeWallet(senderWallet, transaction.getAmount());
            // transfer money to recipient wallet
            transferMoneyToRecipientWallet(transaction.getRecipientWalletIban, transaction.getAmount());
        }
        chargeWallet(senderWallet, transaction.getAmount());
    }
        //todo discuss if this method is necessary or can be delete safely
//    @Override
//    public Transaction updateTransaction(User user, Transaction transaction, int wallet_id) {
//        verifyWallet(wallet_id, user);
//        validateTransactionAmount(transaction);
//        return transactionService.updateTransaction(user, transaction, wallet_id);
//    }

    @Override
    public void approveTransaction(User user, int transaction_id, int wallet_id) {
        // TODO To be implemented - TED consider using a transactionObj since
        //  a transaction will be already have been created at this point or alternatively just its id
       WalletToWalletTransaction transactionToBeApproved =
               walletTransactionService.getWalletTransactionById(transaction_id);

//        Transaction transaction = walletTransactionService.getTransactionById(transaction_id);
        walletTransactionService.approveTransaction(transaction);
    }

    @Override
    public void cancelTransaction(User user, int transaction_id, int wallet_id) {
        // TODO To be implemented - TED consider using a transactionObj since
        //  a transaction will be already have been created at this point or alternatively just its id

        WalletToWalletTransaction transactionToBeCancelled =
                walletTransactionService.getTransactionById(transaction_id);

        walletTransactionService.cancelTransaction(transaction);
    }

    //todo have request and response handling extracted inside a separate method
    // after ensuring functionality is working as intended - Ted
    @Override
    public CardToWalletTransaction transactionWithCard(User user, int card_id, int wallet_id,
                                                       CardToWalletTransaction cardTransaction) {
        WebClient.UriSpec<WebClient.RequestBodySpec> uriSpec = webClient.method(HttpMethod.POST);
        WebClient.RequestBodySpec bodySpec = uriSpec.uri(URI.create(DUMMY_API_COMPLETE_URL));
        Wallet wallet = getWalletById(user, wallet_id);
        Card card = cardService.getCard(card_id, user);
        CardForAddingMoneyToWalletDto cardDto = cardMapper.toDummyApiDto(card);
        WebClient.RequestHeadersSpec<?> headersSpec = bodySpec.bodyValue(cardDto);
        WebClient.ResponseSpec responseSpec = populateResponseSpec(headersSpec);
        Mono<String> response = headersSpec.retrieve().bodyToMono(String.class);
        if (response.block().equals(APPROVED_TRANSFER)) {
            wallet.setBalance(cardTransaction.getAmount() + wallet.getBalance());
            walletRepository.update(wallet);
            cardTransactionService.approveTransaction(cardTransaction, user, card);
        }
        if (response.block().equals(DECLINED_TRANSFER)) {
            cardTransactionService.declineTransaction(cardTransaction, user, card);
        }
        return cardTransaction;
    }

//    private void checkUserPartOfWallet(int user_id, int wallet_id) {
//        // TODO this method should throw exception if user is not part of wallet - LYUBIMA
//        if (!walletRepository.checkIfUserIsPartOfWallet(user_id, wallet_id)) {
//            throw new UnauthorizedOperationException("You are not part of this wallet");
//        }
//    }

    private void checkWalletOwnership(User user, Wallet wallet) {
        if (!walletRepository.checkWalletOwnership(user.getId(), wallet.getWalletId()) == 1 && !userService.verifyAdminAccess(user)) {
            throw new UnauthorizedOperationException(UNAUTHORIZED_OPERATION_ERROR_MESSAGE);
        }
    }

    private Wallet checkWalletExistence(int wallet_id) {
//        if (walletRepository.getWalletById(wallet_id) == null) {
//            throw new EntityNotFoundException("Wallet does not exist");
//        }
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

    //todo discuss checkWalletExistence -- Ted, Lyuba
    private Wallet verifyWallet(int wallet_id, User user) {
        Wallet wallet;
        wallet = checkWalletExistence(wallet_id);
        checkWalletOwnership(user, wallet);
        return wallet;
    }

    public Wallet checkRecipientWalletExistence(String ibanTo) {
        return walletRepository.getByStringField("iban", ibanTo);
    }

    private void checkWalletBalance(Wallet wallet, double amount) {
        if (wallet.getBalance() <= amount) {
            throw new InsufficientFundsException("wallet", wallet.getIban(), wallet.getBalance(), amount);
        }
    }

    private void chargeWallet(Wallet wallet, double amount) {
        double newBalance = wallet.getBalance() - amount;
        wallet.setBalance(newBalance);
        walletRepository.update(wallet);
    }

    private void transferMoneyToRecipientWallet(String ibanTo, double amount) {
        Wallet recipientWallet = walletRepository.getByStringField("iban", ibanTo);
        double newWalletBalance = recipientWallet.getBalance() + amount;
        recipientWallet.setBalance(newWalletBalance);
        walletRepository.update(recipientWallet);
    }
}
