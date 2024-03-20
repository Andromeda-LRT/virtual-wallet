package com.virtualwallet.controllers.rest;

import com.virtualwallet.exceptions.*;
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
import io.swagger.v3.oas.annotations.Operation;
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
import static com.virtualwallet.model_helpers.SwaggerConstantHelper.*;

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

    @Operation(summary = GET_ALL_WALLETS_SUMMARY, description = GET_ALL_WALLETS_DESCRIPTION)
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

    @Operation(summary = GET_WALLET_BY_ID_SUMMARY, description = GET_WALLET_BY_ID_DESCRIPTION)
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

    @Operation(summary = CREATE_WALLET_SUMMARY, description = CREATE_WALLET_DESCRIPTION)
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

    @Operation(summary = UPDATE_WALLET_SUMMARY, description = UPDATE_WALLET_DESCRIPTION)
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

    @Operation(summary = DELETE_WALLET_SUMMARY, description = DELETE_WALLET_DESCRIPTION)
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

    @Operation(summary = GET_WALLET_TRANSACTIONS_SUMMARY, description = GET_WALLET_TRANSACTIONS_DESCRIPTION)
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
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(transactionResponseMapper.convertWalletTransactionsToDto(walletToWalletTransactionList));
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @Operation(summary = GET_CARD_WALLET_TRANSACTIONS_SUMMARY, description = GET_CARD_WALLET_TRANSACTIONS_DESCRIPTION)
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
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(transactionResponseMapper.convertCardTransactionsToDto(cardToWalletTransactionList));
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = GET_TRANSACTION_BY_ID_SUMMARY, description = GET_TRANSACTION_BY_ID_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
    @GetMapping("/{wallet_id}/transactions/{transaction_id}")
    public ResponseEntity<?> getTransactionById(@RequestHeader HttpHeaders headers,
                                                @PathVariable int wallet_id,
                                                @PathVariable int transaction_id) {
        try {
            User user = authHelper.tryGetUser(headers);
            WalletToWalletTransaction walletToWalletTransaction
                    = walletService.getTransactionById(user, wallet_id, transaction_id);
            TransactionResponseDto transaction = transactionResponseMapper.convertToDto(walletToWalletTransaction);
            return ResponseEntity.status(HttpStatus.OK).body(transaction);
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = CREATE_TRANSACTION_SUMMARY, description = CREATE_TRANSACTION_DESCRIPTION)
    @SecurityRequirement(name = AUTHORIZATION)
    @PostMapping("/{wallet_id}/transactions")
    public ResponseEntity<?> createTransaction(@RequestHeader HttpHeaders headers,
                                               @PathVariable int wallet_id,
                                               @RequestBody @Valid TransactionDto transactionDto) {
        try {
            User user = authHelper.tryGetUser(headers);
            userService.isUserBlocked(user);
            WalletToWalletTransaction walletToWalletTransaction
                    = transactionMapper.fromDto(transactionDto, user, wallet_id);
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

    @Operation(summary = CREATE_TRANSACTION_WITH_CARD_SUMMARY, description = CREATE_TRANSACTION_WITH_CARD_DESCRIPTION)
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
    @Operation(summary = GET_RECIPIENT_SUMMARY, description = GET_RECIPIENT_DESCRIPTION)
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
            authHelper.tryGetUser(headers);
            List<User> recipient = walletService.getRecipient(userFilter);
            return userMapper.toRecipientDto(recipient);
        } catch (UnauthorizedOperationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = ADD_USER_TO_WALLET_SUMMARY, description = ADD_USER_TO_WALLET_DESCRIPTION)
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
        } catch (LimitReachedException | DuplicateEntityException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = REMOVE_USER_FROM_WALLET_SUMMARY, description = REMOVE_USER_FROM_WALLET_DESCRIPTION)
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

    @Operation(summary = GET_ALL_WALLET_USERS_SUMMARY, description = GET_ALL_WALLET_USERS_DESCRIPTION)
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