package com.virtualwallet.controllers.rest;

import com.virtualwallet.exceptions.EntityNotFoundException;
import com.virtualwallet.exceptions.InsufficientFundsException;
import com.virtualwallet.exceptions.LimitReachedException;
import com.virtualwallet.exceptions.UnauthorizedOperationException;
import com.virtualwallet.model_helpers.AuthenticationHelper;
import com.virtualwallet.model_helpers.CardTransactionModelFilterOptions;
import com.virtualwallet.model_helpers.UserModelFilterOptions;
import com.virtualwallet.model_helpers.WalletTransactionModelFilterOptions;
import com.virtualwallet.model_mappers.TransactionMapper;
import com.virtualwallet.model_mappers.TransactionResponseMapper;
import com.virtualwallet.model_mappers.UserMapper;
import com.virtualwallet.model_mappers.WalletMapper;
import com.virtualwallet.models.CardToWalletTransaction;
import com.virtualwallet.models.WalletToWalletTransaction;
import com.virtualwallet.models.User;
import com.virtualwallet.models.Wallet;
import com.virtualwallet.models.input_model_dto.CardTransactionDto;
import com.virtualwallet.models.response_model_dto.RecipientResponseDto;
import com.virtualwallet.models.response_model_dto.TransactionResponseDto;
import com.virtualwallet.services.contracts.UserService;
import com.virtualwallet.services.contracts.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.virtualwallet.models.input_model_dto.WalletDto;
import com.virtualwallet.models.input_model_dto.TransactionDto;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static com.virtualwallet.model_helpers.ModelConstantHelper.AUTHORIZATION;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    private final WalletService walletService;
    private final WalletMapper walletMapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationHelper authHelper;
    private final TransactionResponseMapper transactionResponseMapper;
    private final TransactionMapper transactionMapper;

    public WalletController(UserService userService,
                            AuthenticationHelper authHelper,
                            WalletService walletService,
                            WalletMapper walletMapper,
                            UserMapper userMapper,
                            TransactionResponseMapper transactionResponseMapper,
                            TransactionMapper transactionMapper) {
        this.userService = userService;
        this.authHelper = authHelper;
        this.walletService = walletService;
        this.walletMapper = walletMapper;
        this.userMapper = userMapper;
        this.transactionResponseMapper = transactionResponseMapper;
        this.transactionMapper = transactionMapper;
    }

    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping
    public ResponseEntity<?> getAllWallets(@RequestHeader HttpHeaders headers) {
        try {
            User user = authHelper.tryGetUser(headers);
            List<Wallet> walletList = walletService.getAllWallets(user);
            return ResponseEntity.status(HttpStatus.OK).body(walletList);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/{id}")
    public ResponseEntity<?> getWalletById(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authHelper.tryGetUser(headers);
            Wallet wallet = walletService.getWalletById(user, id);
            return ResponseEntity.status(HttpStatus.OK).body(wallet);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @SecurityRequirement(name = AUTHORIZATION)
    @PostMapping()
    public ResponseEntity<?> createWallet(@RequestHeader HttpHeaders headers,
                                          @RequestBody @Valid WalletDto walletDto) {
        try {
            User user = authHelper.tryGetUser(headers);
            Wallet wallet = walletMapper.fromDto(walletDto);
            walletService.createWallet(user, wallet);
            return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (LimitReachedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @SecurityRequirement(name = AUTHORIZATION)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateWallet(@RequestHeader HttpHeaders headers,
                                          @RequestBody @Valid WalletDto walletDto,
                                          @PathVariable int id) {
        try {
            User user = authHelper.tryGetUser(headers);
            Wallet wallet = walletMapper.fromDto(walletDto, id, user);
            walletService.updateWallet(user, wallet);
            return ResponseEntity.status(HttpStatus.OK).body(wallet);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @SecurityRequirement(name = AUTHORIZATION)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWallet(@RequestHeader HttpHeaders headers, @PathVariable int id) {
        try {
            User user = authHelper.tryGetUser(headers);
            walletService.delete(user, id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/{wallet_id}/transactions")
    public ResponseEntity<?> getWalletTransactionHistory(@RequestHeader HttpHeaders headers,
                                                         @PathVariable int wallet_id,
                                                         @RequestParam(required = false)
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                         LocalDateTime startDate,
                                                         @RequestParam(required = false)
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                         LocalDateTime endDate,
                                                         @RequestParam(required = false) String sender,
                                                         @RequestParam(required = false) String recipient,
                                                         @RequestParam(required = false) String direction,
                                                         @RequestParam(required = false) String sortBy,
                                                         @RequestParam(required = false) String sortOrder) {
        try {
            WalletTransactionModelFilterOptions transactionModelFilterOptions = new WalletTransactionModelFilterOptions(
                    startDate, endDate, sender, recipient, direction, sortBy, sortOrder);

            User user = authHelper.tryGetUser(headers);
            List<WalletToWalletTransaction> walletToWalletTransactionList =
                    walletService.getUserWalletTransactions(transactionModelFilterOptions, user, wallet_id);
//            return transactionResponseMapper.convertToDto(walletToWalletTransactionList, wallet_id);
            return ResponseEntity.status(HttpStatus.OK).body(transactionResponseMapper.convertWalletTransactionsToDto(walletToWalletTransactionList));
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/card_transactions/{wallet_id}")
    public ResponseEntity<?> getUserCardsTransactionHistory(@RequestHeader HttpHeaders headers,
                                                            @PathVariable int wallet_id,
                                                         @RequestParam(required = false)
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                         LocalDateTime startDate,
                                                         @RequestParam(required = false)
                                                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                         LocalDateTime endDate,
                                                         @RequestParam(required = false) String cardLastFourDigits,
                                                         @RequestParam(required = false) String recipient,
                                                         @RequestParam(required = false) String direction,
                                                         @RequestParam(required = false) String sortBy,
                                                         @RequestParam(required = false) String sortOrder) {
        try {
            CardTransactionModelFilterOptions transactionModelFilterOptions = new CardTransactionModelFilterOptions(
                    startDate, endDate, cardLastFourDigits, recipient, direction, sortBy, sortOrder);

            User user = authHelper.tryGetUser(headers);
            List<CardToWalletTransaction> cardToWalletTransactionList =
                    walletService.getUserCardTransactions(wallet_id, user, transactionModelFilterOptions);
            return ResponseEntity.status(HttpStatus.OK).body(transactionResponseMapper.convertCardTransactionsToDto(cardToWalletTransactionList));
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

//    @GetMapping("/transactions")
//    public List<TransactionResponseDto> getTransactionHistory(@RequestHeader HttpHeaders headers) {
//        try {
//            User user = authHelper.tryGetUser(headers);
//            List<WalletToWalletTransaction> walletToWalletTransactionList = walletService.getAllWalletTransactions(user);
//            return transactionResponseMapper.convertToDto(walletToWalletTransactionList, wallet_id);
//        } catch (UnauthorizedOperationException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
//        }
//    }

    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/{wallet_id}/transactions/{transaction_id}")
    public ResponseEntity<?> getTransactionById(@RequestHeader HttpHeaders headers,
                                                @PathVariable int wallet_id,
                                                @PathVariable int transaction_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            WalletToWalletTransaction walletToWalletTransaction = walletService.getTransactionById(user, wallet_id, transaction_id);
            TransactionResponseDto transaction = transactionResponseMapper.convertToDto(walletToWalletTransaction);
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @SecurityRequirement(name = AUTHORIZATION)
    @PostMapping("/{wallet_id}/transactions")
    public ResponseEntity<?> createTransaction(@RequestHeader HttpHeaders headers,
                                               @PathVariable int wallet_id,
                                               @RequestBody @Valid TransactionDto transactionDto) {
        try {
            User user = authHelper.tryGetUser(headers);
            userService.isUserBlocked(user);
            WalletToWalletTransaction walletToWalletTransaction = transactionMapper.fromDto(transactionDto, user, wallet_id);
            walletService.walletToWalletTransaction(user, wallet_id, walletToWalletTransaction);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(transactionResponseMapper.convertToDto(walletToWalletTransaction));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InsufficientFundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

//    @PutMapping("/{wallet_id}/transactions/{transaction_id}/update")
//    public TransactionResponseDto updateTransaction(@RequestHeader HttpHeaders headers,
//                                            @PathVariable int wallet_id,
//                                            @RequestBody @Valid TransactionDto transactionDto,
//                                            @PathVariable int transaction_id) {
//        try {
//            User user = authHelper.tryGetUser(headers);
//            Transaction transaction = transactionMapper.fromDto(transactionDto, transaction_id);
//            walletService.updateTransaction(user, transaction, wallet_id);
//            return transactionResponseMapper.convertToDto(transaction);
//        } catch (UnauthorizedOperationException e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
//        } catch (EntityNotFoundException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        } catch(InsufficientFundsException e) {
    //throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
//}
//        //Only a transaction that has not been approved can be updated
//    }

    //TODO remove at the end

//    @PutMapping("/{wallet_id}/transactions/{transaction_id}/approve")
//    public ResponseEntity<?> approveTransaction(@RequestHeader HttpHeaders headers,
//                                                @PathVariable int wallet_id,
//                                                @PathVariable int transaction_id) {
//        try {
//            User user = authHelper.tryGetUser(headers);
//            walletService.approveTransaction(user, transaction_id, wallet_id);
//            return ResponseEntity.status(HttpStatus.OK).build();
//        } catch (UnauthorizedOperationException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }
//
//
//    @PutMapping("/{wallet_id}/transactions/{transaction_id}/cancel")
//    public ResponseEntity<?> cancelTransaction(@RequestHeader HttpHeaders headers,
//                                               @PathVariable int wallet_id,
//                                               @PathVariable int transaction_id) {
//        try {
//            User user = authHelper.tryGetUser(headers);
//            walletService.cancelTransaction(user, transaction_id, wallet_id);
//            return ResponseEntity.status(HttpStatus.OK).build();
//        } catch (UnauthorizedOperationException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//        //Only a transaction that has NOT been approved can be canceled
//    }

    @SecurityRequirement(name = AUTHORIZATION)
    @PostMapping("/{wallet_id}/transactions/{card_id}")
    public ResponseEntity<?> createTransactionWithCard(@RequestHeader HttpHeaders headers,
                                                       @PathVariable int wallet_id,
                                                       @PathVariable int card_id,
                                                       @RequestBody CardTransactionDto cardTransactionDto) {
        try {
            User user = authHelper.tryGetUser(headers);
            CardToWalletTransaction cardTransaction = transactionMapper.fromDto(cardTransactionDto);
            CardToWalletTransaction transactionResult = walletService
                    .transactionWithCard(user, card_id, wallet_id, cardTransaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(transactionResult);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * @param headers     Authenticated user
     * @param username    name of recipient
     * @param email       email of recipient
     * @param phoneNumber phoneNumber of recipient
     * @return returns a list <b>only</b> of recipients that have created wallets,
     * containing their username and their created wallets' ibans.
     */
    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/recipient")
    public List<RecipientResponseDto> getRecipient(@RequestHeader HttpHeaders headers,
                                                   @RequestParam(required = false) String username,
                                                   @RequestParam(required = false) String email,
                                                   @RequestParam(required = false) String phoneNumber,
                                                   @RequestParam(required = false) String sortBy,
                                                   @RequestParam(required = false) String orderBy) {

        UserModelFilterOptions userFilter = new UserModelFilterOptions(
                username, email, phoneNumber, sortBy, orderBy);
        try {
            User loggedUser = authHelper.tryGetUser(headers);
            List<User> recipient = walletService.getRecipient(userFilter);
            return userMapper.toRecipientDto(recipient);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
        //todo consider if badRequest would need to be added, due to userFilter
    }

    @SecurityRequirement(name = AUTHORIZATION)
    @PostMapping("/{wallet_id}/addUserToWallet/{user_id}")
    public ResponseEntity<?> addUserToWallet(@RequestHeader HttpHeaders headers,
                                             @PathVariable int wallet_id,
                                             @PathVariable int user_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            walletService.addUserToWallet(user, wallet_id, user_id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (LimitReachedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @SecurityRequirement(name = AUTHORIZATION)
    @DeleteMapping("/{wallet_id}/removeUserFromWallet/{user_id}")
    public ResponseEntity<?> removeUserFromWallet(@RequestHeader HttpHeaders headers,
                                                  @PathVariable int wallet_id,
                                                  @PathVariable int user_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            walletService.removeUserFromWallet(user, wallet_id, user_id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/{wallet_id}/users")
    public List<User> getWalletUsers(@RequestHeader HttpHeaders headers,
                                     @PathVariable int wallet_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            return walletService.getWalletUsers(user, wallet_id);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

}